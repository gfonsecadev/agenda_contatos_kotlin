package com.example.minhaagenda

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    //objeto para configuração do menu hamburguer
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setando a toolbar criada
        setSupportActionBar(binding.appBarMain.toolbar)

        //instância do drawerLayout
        val drawerLayout: DrawerLayout = binding.drawerLayout
        //instância do NavigationView
        val navView: NavigationView = binding.navView
        //o navController é a view que será responsável por renderizar os fragments do navigation
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        //configuração da appbar para renderizar o navigation e seus fragments associados.
        //passe um conjunto de fragment através do setof ou passe direto o navController.graph
        //o comportamento do .graph será de voltar para depois selecionar.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_all, R.id.nav_config, R.id.nav_exit), drawerLayout
        )
        // Configurar a AppBar para exibir o botão de up (seta de voltar) quando necessário
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Configurar o NavigationView para manipular as ações de clique
        navView.setupWithNavController(navController)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    //Este método é crucial para garantir um comportamento de navegação adequado quando o botão para cima da ActionBar é pressionado.
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}