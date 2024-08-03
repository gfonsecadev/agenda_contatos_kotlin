package com.example.minhaagenda.ui.main

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.minhaagenda.entities.SharedPreferencesHelper
import com.example.minhaagenda.shared.LaunchersImage
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.shared.PermissionsManager
import com.example.minhaagenda.shared.LauncherPermissions
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import androidx.activity.result.contract.ActivityResultContracts


// Classe principal da atividade que estende AppCompatActivity
class MainActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController
    private lateinit var launcherPermissions: LauncherPermissions
    private  lateinit var launchersImage: LaunchersImage
    private lateinit var preferencesHelper: SharedPreferencesHelper
    private lateinit var imageProfile: ImageView
    private lateinit var textNameProfile: TextView

    // Método chamado na criação da atividade
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurações iniciais

        // Configura a Toolbar e define como a ActionBar da Activity
        setupToolbar()
        // Configura o DrawerLayout e o ActionBarDrawerToggle para o menu lateral
        setupDrawerLayout()
        // Configura o navView para exibir o fragment do  item clicado
        setupNavView()
        // Atualiza o textView do NavHeader com informações do dispositivo
        setupDataDevice()
        //inicilalizando preferencias do usuário
        initializePreferences()
        //inicia os registerForActivityResult para permissôes e seleção de imagens(câmera e galeria)na activity
        registerLaunchersActivity()
        // Configura o ViewModel para gerenciar o estado do AppBarLayout
        setupViewModelAppBar()
        // Configura o listener de clique na imagemView do perfil
        profileImageChoice()
        // Configura o listener de clique no textView do perfil
        profileTextChoice()
    }

    // Inicializa os ViewModels e registra os observadores de permissões e galerias
    private fun registerLaunchersActivity() {
        // Registra e observa permissões
        hasPermissionsActivity()
        // Registra e observa a galeria para seleção de imagens
        takeImageActivity()
        //registerAndObserveLaucherCamera()
    }


    //register para pedir permissão
    private fun hasPermissionsActivity() {
        launcherPermissions = LauncherPermissions()
        // Cria um launcher para solicitar múltiplas permissões e lida com a resposta
        val laucherPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            // Verifica se todas as permissões foram concedidas
            val isGranted = result.all { it.value }
            //metodo que se permissão dada executa uma função (neste caso aqui chama um dialog para escolha da imagem)
            PermissionsManager.executeIfPermissionGranted(isGranted, onPermissionGranted = { dialogImage() }, this)

        }

        //atribui o register para a classe de ajuda (para poder ser lançada em outro momento)
        launcherPermissions.registerLauncherPermissions(laucherPermissions)

    }

    //register para escolha da imagem (galeria ou camera)
    private fun takeImageActivity() {
        launchersImage = LaunchersImage()
        // Cria um launcher para iniciar uma atividade e lida com o resultado
        val intent = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) {result->

            var bitmap: Bitmap? = null
            //se galeria
            val uri = result.data?.data
            uri?.let {
                // Inicializa o helper para acessar preferências
                preferencesHelper = SharedPreferencesHelper(this)

                //implementação para converter a URI em um Bitmap de acordo com o sdk
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)

                } else {//se acima
                    val source = ImageDecoder.createSource(this.contentResolver, it)
                    bitmap = ImageDecoder.decodeBitmap(source)

                }
            }

            //se camera
            val data = result.data?.extras?.get("data")
           data?.let { image->
                bitmap = image as Bitmap
            }

            //se não nulo salva o Bitmap nas preferências convertendo-o para string
            bitmap?.let {
                preferencesHelper.editPreferencesImage(it)
                imageProfile.setImageBitmap(it)}
        }

        //atribui o register para a classe de ajuda (para poder ser lançada em outro momento)
        launchersImage.registerLauncher(intent)

    }


    // Inicializa o objeto de ajuda para as preferências compartilhadas
    private fun initializePreferences() {
        //inicia os objetos para serem utilizados globalmente
        imageProfile = binding.navView.getHeaderView(0).findViewById(R.id.profile_image)
        textNameProfile = binding.navView.getHeaderView(0).findViewById(R.id.profile_name)

        preferencesHelper = SharedPreferencesHelper(this)//classe de ajuda

        //seta as preferencias se definidas
        imageProfile.setImageBitmap(preferencesHelper.getPreferencesImage())//recupera a a imagem salva convertendo-a de string para Bitmap
        textNameProfile.text = preferencesHelper.getPreferencesName()//recupera o nome salvo
    }

    // Método para definir o comportamento do clique na imagem do perfil
    private fun profileTextChoice() {
        textNameProfile.setOnClickListener {
            showEditProfileDialog()//mostra um dialog para edição do nome
        }
    }

    // Exibe um diálogo para editar o nome do perfil
    private fun showEditProfileDialog() {
        val layout = layoutInflater.inflate(R.layout.edit_text_profile_name, null, false)
        val alert = AlertDialog.Builder(this, R.style.Theme_MinhaAgenda_CustomDialog)
        alert.setView(layout)//esse dialog possui um layout próprio

        val dialog: Dialog = alert.create()
        dialog.show()

        // recupera as referência dos elementos do  layout do dialog
        val editText = dialog.findViewById<EditText>(R.id.edit_name_profile)
        val button = dialog.findViewById<Button>(R.id.btn_save_name_profile)

        //preenche o editText com o nome atual salvo em preferências
        //é necessário passar um Editable para um editText este metódo abaixo(nativo) o cria através de uma string(nome atual salvo em preferências)
        editText?.text = Editable.Factory.getInstance().newEditable(preferencesHelper.getPreferencesName())

        //salva o nome digitado em preferências
        button?.setOnClickListener {
            val name = editText?.text.toString().trim()
            if (name.isNotEmpty()) {
                saveProfileName(name)
                dialog.dismiss()
            } else {
                editText?.error = "O nome não pode estar vazio"
            }
        }
    }

    // Salva o nome do perfil nas preferências compartilhadas
    private fun saveProfileName(name: String) {
        preferencesHelper.editPreferencesName(name)
        textNameProfile.text = name
    }

    // Método para definir o comportamento do clique na imagem do perfil
    private fun profileImageChoice() {
        imageProfile.setOnClickListener {
            launcherPermissions.askPermissions()
        }
    }


    // Exibe um diálogo para escolher a imagem de perfil
    private fun dialogImage() {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Escolha uma imagem")
            .setMessage("Escolha imagem da galeria ou capture uma nova imagem!")
            .setPositiveButton("Galeria") { _, _ -> launchersImage.openGallery() }
            .setNegativeButton("Câmera") { _, _ -> launchersImage.openCamera()}
        dialogBuilder.show()
    }

    // Configura a barra de ferramentas (toolbar)
    private fun setupToolbar() {
        setSupportActionBar(binding.appBarMain.toolbar)
    }

    // Configura o DrawerLayout (menu lateral)
    private fun setupDrawerLayout() {
        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.appBarMain.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val content: View = findViewById(R.id.nav_host_fragment_content_main)
                content.translationX = slideOffset * drawerView.width
                content.translationZ = slideOffset * drawerView.width
            }
        })
    }

    // Troca o fragmento de acordo com a opção do menu selecionada
    private fun changeFragmentNavController(fragmentId: Int) {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_fragment)
            .setExitAnim(R.anim.close_fragment)
            .build()
        controller = findNavController(R.id.nav_host_fragment_content_main)
        controller.navigate(fragmentId, null, navOptions)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    // Configura o NavigationView (menu lateral)
    private fun setupNavView() {
        val navView: NavigationView = binding.navView
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_all -> changeFragmentNavController(R.id.nav_all)
                R.id.nav_config -> changeFragmentNavController(R.id.nav_config)
                R.id.nav_exit -> changeFragmentNavController(R.id.nav_exit)
            }
            true
        }
    }

    // Configura os dados do dispositivo no cabeçalho do NavigationView
    private fun setupDataDevice() {
        val brand = Build.BRAND.takeIf { it.isNotBlank() }?.replaceFirstChar { it.uppercase() } ?: "Unknown"
        val model = Build.MODEL.takeIf { it.isNotBlank() } ?: "Unknown"
        val dataDevice = "$brand $model"
        setDeviceNavHeaderName(dataDevice)
    }

    // Define o nome do dispositivo no cabeçalho do NavigationView
    private fun setDeviceNavHeaderName(data: String) {
        val textHeader = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.textHeader)
        textHeader.text = data
    }

    // Configura o AppBarViewModel para controlar o estado da AppBar
    private fun setupViewModelAppBar() {
        val viewModelShared = ViewModelProvider(this).get(AppBarViewModel::class.java)
        viewModelShared.appBarLayoutState.observe(this) { expanded ->
            binding.appBarMain.appBarContact.setExpanded(expanded)
        }
    }

    // Infla o menu de opções na barra de ferramentas
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }
}
