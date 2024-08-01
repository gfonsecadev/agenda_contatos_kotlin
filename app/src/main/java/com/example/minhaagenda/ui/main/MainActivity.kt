package com.example.minhaagenda.ui.main

import android.app.Dialog
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
import com.example.minhaagenda.shared.LaunchersViewModel
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.shared.Permissions
import com.example.minhaagenda.shared.PermissionsViewModel
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels


// Classe principal da atividade que estende AppCompatActivity
class MainActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController
    private val permissionsViewModel: PermissionsViewModel by viewModels()
    private  val launchersViewModel: LaunchersViewModel by viewModels()
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
        //inicializa o ViewModel de permissões
        observeAndHandleRegisters()
        // Configura o ViewModel para gerenciar o estado do AppBarLayout
        setupViewModelAppBar()
        // Configura o listener de clique na imagemView do perfil
        profileImageChoice()
        // Configura o listener de clique no textView do perfil
        profileTextChoice()
    }

    // Inicializa os ViewModels e registra os observadores de permissões e galerias
    fun observeAndHandleRegisters() {
        // Registra e observa permissões
        registerAndObservePermissions()
        // Registra e observa a galeria para seleção de imagens
        registerAndObserveLaucherGallery()
        //registerAndObserveLaucherCamera()
    }

    private fun registerAndObserveLaucherCamera() {
        val intent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Verifica se a atividade retornou com sucesso e dados não nulos
            if (result.resultCode == RESULT_OK) {
                // Atualiza o ViewModel com o URI da imagem selecionada
                result.data?.data?.let {url->
                    launchersViewModel.changeLiveData(url)}
            }
        }
        launchersViewModel.registerLauncher(intent)
        launchersViewModel.uri.observe(this,{

        })
    }

    // Registra e observa permissões solicitadas
    fun registerAndObservePermissions() {
        // Cria um launcher para solicitar múltiplas permissões e lida com a resposta
        val laucherPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            // Verifica se todas as permissões foram concedidas
            val isGranted = result.all { it.value }
            // Atualiza o ViewModel com o resultado da permissão
            permissionsViewModel.changeLiveData(isGranted)
        }

        // Registra o launcher de permissões no ViewModel
        permissionsViewModel.registerLauncherPermissions(laucherPermissions)

        // Observa o LiveData de permissões concedidas
        permissionsViewModel.isPermissionGranted.observe(this) { isGranted ->
            // Executa uma ação se as permissões forem concedidas
            Permissions.executeIfPermissionGranted(isGranted, onPermissionGranted = { dialogImage() }, this)
        }
    }

    // Registra e observa o launcher da galeria para seleção de imagens
    fun registerAndObserveLaucherGallery() {
        // Cria um launcher para iniciar uma atividade e lida com o resultado
        val intent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Verifica se a atividade retornou com sucesso e dados não nulos
            if (result.resultCode == RESULT_OK) {
                // Atualiza o ViewModel com o URI da imagem selecionada
                result.data?.data?.let {url->
                launchersViewModel.changeLiveData(url)}
            }
        }

        // Registra o launcher da galeria no ViewModel
        launchersViewModel.registerLauncher(intent)

        // Observa o LiveData do URI da imagem selecionada
        launchersViewModel.uri.observe(this) { uri ->
            // Define a URI da imagem no ImageView
            imageProfile.setImageURI(uri)

            // Inicializa o helper para acessar preferências
            preferencesHelper = SharedPreferencesHelper(this)

            // Converte a URI em um Bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            // Salva o Bitmap nas preferências convertendo-o para string
            preferencesHelper.editPreferencesImage(bitmap)
        }
    }


    // Inicializa o objeto de ajuda para as preferências compartilhadas
    private fun initializePreferences() {
        preferencesHelper = SharedPreferencesHelper(this)
        imageProfile = binding.navView.getHeaderView(0).findViewById(R.id.profile_image)
        imageProfile.setImageBitmap(preferencesHelper.getPreferencesImage())

        textNameProfile = binding.navView.getHeaderView(0).findViewById(R.id.profile_name)
        textNameProfile.text = preferencesHelper.getPreferencesName()
    }

    // Método para definir o comportamento do clique na imagem do perfil
    private fun profileTextChoice() {
        textNameProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    // Exibe um diálogo para editar o nome do perfil
    private fun showEditProfileDialog() {
        val layout = layoutInflater.inflate(R.layout.edit_text_profile_name, null, false)
        val alert = AlertDialog.Builder(this, R.style.Theme_MinhaAgenda_CustomDialog)
        alert.setView(layout)

        val dialog: Dialog = alert.create()
        dialog.show()

        val editText = dialog.findViewById<EditText>(R.id.edit_name_profile)
        val button = dialog.findViewById<Button>(R.id.btn_save_name_profile)

        editText?.text = Editable.Factory.getInstance().newEditable(preferencesHelper.getPreferencesName())

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
            permissionsViewModel.askPermissions()
        }
    }


    // Exibe um diálogo para escolher a imagem de perfil
    fun dialogImage() {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Escolha uma imagem")
            .setMessage("Escolha imagem da galeria ou capture uma nova imagem!")
            .setPositiveButton("Galeria") { _, _ -> launchersViewModel.openGallery() }
            .setNegativeButton("Câmera") { _, _ -> launchersViewModel.openCamera()}
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
