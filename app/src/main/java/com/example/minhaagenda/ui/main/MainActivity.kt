package com.example.minhaagenda.ui.main

import android.Manifest
import android.app.Dialog
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
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

// Classe principal da atividade que estende AppCompatActivity
class MainActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController
    private lateinit var permissionsViewModel: PermissionsViewModel
    private lateinit var launchersViewModel: LaunchersViewModel
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
        initializeViewModel()
        // Configura o ViewModel para gerenciar o estado do AppBarLayout
        setupViewModelAppBar()
        // Configura o listener de clique na imagemView do perfil
        profileImageChoice()
        // Configura o listener de clique no textView do perfil
        profileTextChoice()
    }

    // Inicializa os ViewModels
    private fun initializeViewModel() {
        permissionsViewModel = ViewModelProvider(this).get(PermissionsViewModel::class.java)
        permissionsViewModel.permissions(this)
        permissionsViewModel.isPermissionGranted.observe(this) { isGranted ->
            if (isGranted) {
                dialogImage()
            } else {
                Permissions.requestPermissionsAgain(this)
            }
        }

        launchersViewModel = ViewModelProvider(this).get(LaunchersViewModel::class.java)
        launchersViewModel.launcherGallery(this)
        launchersViewModel.uri.observe(this) { uri ->
            imageProfile.setImageURI(uri)
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
            askPermissions()
        }
    }

    // Solicita as permissões necessárias para acessar a galeria ou câmera
    // Este método adapta a solicitação de permissões com base na versão do sistema operacional,pois houveram mudanças de como isso deve ser feito
    private fun askPermissions() {
        if (Build.VERSION.SDK_INT < VERSION_CODES.TIRAMISU) {
            // Se a versão do SDK for anterior à Android 13 (TIRAMISU)
            permissionsViewModel.askPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE, // Permissão para ler arquivos de armazenamento externo
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, // Permissão para gravar arquivos de armazenamento externo
                    Manifest.permission.CAMERA // Permissão para usar a câmera do dispositivo
                )
            )
        } else {
            // Se a versão do SDK for Android 13 (TIRAMISU) ou superior
            permissionsViewModel.askPermissions(
                arrayOf(
                    Manifest.permission.CAMERA, // Permissão para usar a câmera do dispositivo
                    Manifest.permission.READ_MEDIA_IMAGES // Permissão para acessar imagens de mídia (substitui a permissão READ_EXTERNAL_STORAGE em Android 13)
                )
            )
        }

    }

    // Exibe um diálogo para escolher a imagem de perfil
    private fun dialogImage() {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Escolha uma imagem")
            .setMessage("Escolha imagem da galeria ou capture uma nova imagem!")
            .setPositiveButton("Galeria") { _, _ -> launchersViewModel.openGallery() }
            .setNegativeButton("Câmera") { _, _ -> }
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
