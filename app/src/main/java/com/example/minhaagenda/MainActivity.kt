package com.example.minhaagenda

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.get
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.minhaagenda.ui.addContacts.AddContactFragment
import com.example.minhaagenda.ui.allContacts.AllContactsFragment
import com.example.minhaagenda.ui.exitContacts.ExitContactragment
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.navigation.NavigationView
import kotlin.concurrent.fixedRateTimer


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //objeto para configuração do menu hamburguer
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setando a toolbar criada
        setSupportActionBar(binding.appBarMain.toolbar)

        //instância do drawerLayout
        drawerLayout = binding.drawerLayout
        //instância do NavigationView
        val navView: NavigationView = binding.navView
        //instãnciamos o toggle passando a activity, o drawer layout, a toobar e uma string de open e close (para ajudar usuários com deficiências visuais a entenderem o que o ícone do menu hambúrguer representa em cada estado)
        toggle = ActionBarDrawerToggle(this,drawerLayout,binding.appBarMain.toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        //o passamos para o drawerLayout através do listener
        drawerLayout.addDrawerListener(toggle)
        controller = findNavController(R.id.nav_host_fragment_content_main)

        //implementado no final da activity
        navView.setNavigationItemSelectedListener(this)

        // Sincroniza o estado do ícone do Drawer com o DrawerLayout
        toggle.syncState()


        //metodo para trabalhar como as animações do toggle
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val content: View = findViewById(R.id.nav_host_fragment_content_main)
                content.translationX = slideOffset * drawerView.width
                content.translationZ = slideOffset * drawerView.width
            }
        })

        //instãncia do SharedViewModel
        val viewModelShared = ViewModelProvider(this).get(SharedViewModel::class.java)

        //o LiveData appBarLayoutState será observado
        //será chamada quando o metodo setAppBarLayoutState nos fragments  for acionado
        viewModelShared.appBarLayoutState.observe(this) { expanded ->//este valor é o valor da MutableLiveData _appBarLayoutState
            val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_contact)
            appBarLayout?.setExpanded(expanded)
        }
    }
    //metodo para retornar um fragment para ser mostrado
    fun replaceFragment(fragmentId: Int){
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_fragment)
            .setExitAnim(R.anim.close_fragment)
            .build()
        controller.navigate(fragmentId,null,navOptions )
        drawerLayout.closeDrawer(GravityCompat.START)

    }


    //metodo que infla o menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    //metodo obrigatório da interface OnNavigationItemSelectedListener implementado
    //será passada mais acima pela navView.setNavigationItemSelectedListener
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_all -> replaceFragment(R.id.nav_all)
            R.id.nav_config -> replaceFragment(R.id.nav_config)
            R.id.nav_exit -> replaceFragment(R.id.nav_exit)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


}