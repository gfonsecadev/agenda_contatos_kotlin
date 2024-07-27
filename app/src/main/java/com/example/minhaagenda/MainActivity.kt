package com.example.minhaagenda

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener


class MainActivity : AppCompatActivity() {
    //objeto para configuração do menu hamburguer
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController
    private lateinit var permissionsLauncher : ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a Toolbar e define como a ActionBar da Activity
        setupToolbar()
        // Configura o DrawerLayout e o ActionBarDrawerToggle para o menu lateral
        setupDrawerLayout()
        // Configura o navView para exibir o fragment do  item clicado
        setupNavView()
        // Atualiza o textView do NavHeader com informações do dispositivo
        setupDataDevice()
        // Configura o ViewModel para gerenciar o estado do AppBarLayout
        setupViewModel()
        permissions()
        profileImageChoice()

    }

    fun profileImageChoice(){
        var imageView = binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.profile_image)
        imageView.setOnClickListener {
            //A partir do Android 13 (API 33), não é mais necessário solicitar permissões amplas para leitura e escrita no armazenamento externo. O gerenciamento de permissões é feito automaticamente pelo launcher de arquivos, que concede acesso parcial apenas aos arquivos específicos que o usuário seleciona.
            //se android for abaixo de 13, pedimos todas as permissôes
            if(VERSION.SDK_INT < VERSION_CODES.TIRAMISU){
                permissionsLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA))
            }else{//se for 13 ou acima somente pedimos a camera
                permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA))

            }
        }
    }

    fun dialogImage(){
        var dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Escolha uma imagem")
        dialogBuilder.setMessage("Escolha imagem da geleria ou capture uma nova imagem!")
        dialogBuilder.setPositiveButton("Galeria"
        ) { dialog, which ->

        }

        dialogBuilder.setNegativeButton("Câmera"
        ) { dialog, which ->

        }

        var dialog = dialogBuilder.create()
        dialog.show()
    }



    fun permissions(){
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(),
            object : ActivityResultCallback<Map<String,Boolean>>{
                override fun onActivityResult(result: Map<String, Boolean>) {
                    if(result.values.size>0) {
                        var denied = false
                        for (permission in result.values) {
                            if (!permission) {
                                denied = true
                            }
                        }
                        //se denied = true significa que há permissôes negadas
                        if (denied) {
                            requestPermissionAgain()
                        }else{
                            dialogImage()
                        }
                    }
                }

            })

    }

    //metodo para permissôes quando negadas
    private fun requestPermissionAgain() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle("Permissões Necessárias")
        alertBuilder.setMessage("Para continuar, precisamos da sua permissão para acessar a câmera e a galeria. Isso é necessário para que você possa escolher ou capturar uma imagem para o perfil. Por favor, vá para as Configurações e habilite as permissões necessárias.")
        alertBuilder.setPositiveButton("Ir para Configurações") { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }
        alertBuilder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        val alert = alertBuilder.create()
        alert.show()



    }


    fun setupToolbar(){
        //setando a toolbar criada
        setSupportActionBar(binding.appBarMain.toolbar)
    }

    fun setupDrawerLayout(){
        //instância do drawerLayout
        drawerLayout = binding.drawerLayout

        //instãnciamos o toggle passando a activity, o drawer layout, a toobar e uma string de open e close (para ajudar usuários com deficiências visuais a entenderem o que o ícone do menu hambúrguer representa em cada estado)
        toggle = ActionBarDrawerToggle(this,drawerLayout,binding.appBarMain.toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        //o passamos para o drawerLayout através do listener
        drawerLayout.addDrawerListener(toggle)
        // Sincroniza o estado do ícone do Drawer com o DrawerLayout
        toggle.syncState()

        //metodo para trabalhar com as animações do drawerLayout
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val content: View = findViewById(R.id.nav_host_fragment_content_main)
                content.translationX = slideOffset * drawerView.width
                content.translationZ = slideOffset * drawerView.width
            }
        })
    }

    //metodo para retornar um fragment com animação de entrada e saida
    fun changeFragmentNavController(fragmentId: Int){
        val navOptions = NavOptions.Builder()//criação da animação para a troca de fragment
            .setEnterAnim(R.anim.enter_fragment)
            .setExitAnim(R.anim.close_fragment)
            .build()
        controller = findNavController(R.id.nav_host_fragment_content_main)
        controller.navigate(fragmentId,null,navOptions )
        drawerLayout.closeDrawer(GravityCompat.START)//fecha o drawerLayout
    }

    fun setupNavView(){
        //instância do NavigationView
        val navView: NavigationView = binding.navView

        navView.setNavigationItemSelectedListener(object : OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.nav_all -> changeFragmentNavController(R.id.nav_all)
                    R.id.nav_config -> changeFragmentNavController(R.id.nav_config)
                    R.id.nav_exit -> changeFragmentNavController(R.id.nav_exit)
                }
                return true
            }

        })
    }

    //metódo para recuperar o nome do dispositivo
    fun setupDataDevice(){
        val brand = Build.BRAND.takeIf { it.isNotBlank() }?.replaceFirstChar { it.uppercase() } ?: "Unknown"
        val model = Build.MODEL.takeIf { it.isNotBlank() } ?: "Unknown"
        val dataDevice = "$brand $model"
        setDeviceNavHeaderName(dataDevice)
    }

    //configurando a exibição do nome do dispositivo no textView da NavHeader
    fun setDeviceNavHeaderName(data:String){
        val textHeader = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.textHeader)
        textHeader.text = data
    }

    fun setupViewModel(){
        //instãncia do SharedViewModel
        val viewModelShared = ViewModelProvider(this).get(SharedViewModel::class.java)

        //o LiveData appBarLayoutState será observado
        //será chamada quando o metodo setAppBarLayoutState nos fragments  for acionado
        viewModelShared.appBarLayoutState.observe(this) { expanded ->//este valor é o valor da MutableLiveData _appBarLayoutState
            binding.appBarMain.appBarContact.setExpanded(expanded)
        }
    }

    //metodo que infla o menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

}