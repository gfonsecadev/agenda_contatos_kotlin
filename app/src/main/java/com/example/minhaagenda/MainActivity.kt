package com.example.minhaagenda

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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