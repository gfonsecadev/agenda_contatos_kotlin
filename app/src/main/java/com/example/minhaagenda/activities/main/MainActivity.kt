package com.example.minhaagenda.activities.main

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.minhaagenda.shared.SharedPreferencesHelper
import com.example.minhaagenda.shared.LaunchersImage
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.shared.PermissionsManager
import com.example.minhaagenda.shared.LauncherPermissions
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.clearListSelectedContact
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.getListSelectedContacts
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.getSizeSelectedContacts
import com.example.minhaagenda.shared.contactListToVcard
import com.example.minhaagenda.shared.exportContact

import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Classe principal da atividade que estende AppCompatActivity
class MainActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController
    private lateinit var launcherPermissions: LauncherPermissions
    private  lateinit var launchersImage: LaunchersImage
    private lateinit var preferencesHelper: SharedPreferencesHelper
    private lateinit var imageProfile: ImageView
    private lateinit var textNameProfile: TextView
    private val viewModelShared: AppBarViewModel by viewModels()
    private val viewModelMain : ViewModelMain by viewModels {ViewModelMainFactory(application) }

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
            //metodo que se permissão dada executa uma função (neste caso aqui chama um dialog para escolha da imagem)senão pede novamente permissões mamuais
            PermissionsManager.executeIfPermissionGranted(isGranted, onPermissionGranted = { dialogImage() }, this)

        }

        //atribui o register para a classe de ajuda (para poder ser lançada em outro momento)
        launcherPermissions.registerLauncherPermissions(laucherPermissions)

    }

    //register para escolha da imagem (galeria ou camera) do perfil
    private fun takeImageActivity() {
        launchersImage = LaunchersImage()
        // Cria um launcher para iniciar uma atividade e lida com o resultado
        val registerTakePicture = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

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
            data?.let { image ->
                bitmap = image as Bitmap
            }

            //se não nulo salva o Bitmap nas preferências convertendo-o para string
            bitmap?.let {
                preferencesHelper.editPreferencesImage(it)
                imageProfile.setImageBitmap(it)
            }

        }

        //atribui o register para a classe de ajuda (para poder ser lançada em outro momento)
            launchersImage.registerLauncher(registerTakePicture)

        }



    // Inicializa o objeto de ajuda para as preferências compartilhadas
    private fun initializePreferences() {
        //inicia os objetos para serem utilizados globalmente
        imageProfile = binding.navView.getHeaderView(0).findViewById(R.id.profile_image)
        textNameProfile = binding.navView.getHeaderView(0).findViewById(R.id.profile_name)

        preferencesHelper = SharedPreferencesHelper(this)//classe de ajuda

        //seta as preferencias se definidas
        Glide.with(this).load(preferencesHelper.getPreferencesImage()).into(imageProfile) //recupera a a imagem salva convertendo-a de string para Bitmap
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
    fun changeFragmentNavController(fragmentId: Int, itemId: Int? = null) {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_fragment)
            .setExitAnim(R.anim.close_fragment)
            .build()
        controller = findNavController(R.id.nav_host_fragment_content_main)
        controller.navigate(fragmentId, null,navOptions)
        drawerLayout.closeDrawer(GravityCompat.START)
        focusItem(itemId)
    }

    //retorna o foco no item da navView chamado
    private fun focusItem(itemId: Int?){
        itemId?.let { navView.menu.findItem(it).isChecked = true }
    }

    // Configura o NavigationView (menu lateral)
    private fun setupNavView() {
        navView = binding.navView
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_item_all -> {
                    changeFragmentNavController(R.id.nav_all)
                    focusItem(item.itemId)
                }
                R.id.nav_item_add_contact -> {
                    changeFragmentNavController(R.id.nav_add_contact)
                    focusItem(item.itemId)
                }
                R.id.nav_item_exit -> {
                    changeFragmentNavController(R.id.nav_exit)
                    focusItem(item.itemId)
                }
            }
            true
        }
    }

    // Configura os dados do dispositivo no cabeçalho do NavigationView
    private fun setupDataDevice() {
        //recupera acesso ao textView na navView para alterar o nome do dispositivo
        val textHeader = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.textHeader)
        // Define o nome do dispositivo no cabeçalho do NavigationView
        textHeader.text = viewModelMain.dataDevice()
    }


    // Configura o AppBarViewModel para controlar o estado da AppBar
    private fun setupViewModelAppBar() {
            viewModelShared.appBarLayoutState.observe(this) { expanded ->
            binding.appBarMain.appBarContact.setExpanded(expanded)
        }
    }

    // Infla o menu de opções na barra de ferramentas com base na condição 'isSelected'
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Verifica se a condição 'isSelected' é verdadeira para inflar o menu para contato(s) selecionado(s)
        return if (getSizeSelectedContacts() > 0) {
            // Infla o menu definido em 'menu_selected_contacts.xml' na barra de ferramentas
            menuInflater.inflate(R.menu.menu_selected_contacts, menu)
            true // Retorna true para indicar que o menu foi inflado com sucesso
        } else {
            //Infla o menu de pesquisa de contatos
             menuInflater.inflate(R.menu.menu_search,menu)
            val searchContact = menu.findItem(R.id.search_contact).actionView as SearchView
            searchContact.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isBlank()){
                        typedNameToSearch = ""
                        allContacts()
                    }else {
                        typedNameToSearch = newText
                        contactFound(newText)
                    }
                    return true
                }

            })
            true
        }
    }


    //opcões no menu de itens selecionados
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_menu -> {
                if (getSizeSelectedContacts() > 0) {
                    lifecycleScope.launch {
                        // Deleta os contatos selecionados
                        viewModelMain.deleteSelectedContacts(getListSelectedContacts())

                        // Exibe o Snackbar informando o sucesso da operação
                        loadSnackBar()

                        // Recarrega a lista de contatos para refletir a exclusão
                        reloadContactList()
                        delay(50)
                        // Limpa os contatos selecionados
                        clearListSelectedContact()

                        // Invalida o menu para remover o ícone de delete
                        invalidateOptionsMenu()
                    }
                }
            }
            R.id.share_menu -> {
                shareContacts()
            }
        }
        return true
    }

    fun shareContacts(){
        if (getSizeSelectedContacts() > 0) { // Verifica se há contatos selecionados
            // Converte a lista de contatos selecionados em um arquivo vCard com a extension contactListToVcard
            val listVcard = getListSelectedContacts().contactListToVcard()
            listVcard?.let {
                //função criada para exportar arquivo de contatos por uma intent
                exportContact(it,this)
            }
        }
    }

    //configurando uma snackBar
    private fun loadSnackBar(){
        // Exibe o Snackbar informando o sucesso da operação
        val snackbar = Snackbar.make(
            binding.root,
            "${getSizeSelectedContacts()} contatos excluídos!",
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(getColor(R.color.main_orange))
        snackbar.show()
    }

    private fun getInstanceAllFragment(): AllContactsFragment {
        // Encontra o NavHostFragment dentro do layout atual
        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        // Recupera o fragmento que contém todos os contatos
        val allFragment = navFragment.childFragmentManager.primaryNavigationFragment as AllContactsFragment
        // Chama o método do fragmento para recarregar a lista de contatos
        return allFragment
    }

    // Função para recarregar a lista de contatos após uma alteração
    private fun reloadContactList() {
        getInstanceAllFragment().reloadContactList()
    }

    // Função para procurar os contatos na searchView
    private fun contactFound(name: String){
        getInstanceAllFragment().searchContacts(name)
    }

    private fun allContacts(){
        getInstanceAllFragment().getAllContacts()
    }

    override fun onStart() {
        super.onStart()
        // Configura o navView para exibir o fragment do  item clicado
        setupNavView()
        //seleciona o focu na inicialização da activity no item que mostra todos os contatos
        focusItem(R.id.nav_item_all)
    }

    companion object{
        var typedNameToSearch:String = ""
    }

}
