package com.example.minhaagenda.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//view model para exibição ou recolhimento da AppBar
class AppBarViewAndSearchViewModel : ViewModel() {

    //essa variavel que sofrerá mudançãs através do método setAppBarLayoutState
    private val _appBarLayoutState = MutableLiveData<Boolean>()
    private val _searchMenuState = MutableLiveData<Int>()

    //essas variaveis que serão observadas na activity
    // sempre será chamada quando a variavel _appBarLayoutState ou searchMenuState sofrer alteração através do metodo setAppBarLayoutState e setShowOrGoneSearchMenu
    val appBarLayoutState: LiveData<Boolean> get() = _appBarLayoutState
    val searchMenuState: LiveData<Int> get() = _searchMenuState

    // Função para atualizar o estado do AppBarLayout
    //será chamada nos fragments
    fun setAppBarLayoutState(expanded: Boolean) {
        //valor passado vai alterar a variavel abaixo disparando assim o observador(appBarLayoutState) na activity
        //dentro deste observer na activity que contém a logica para fechar a AppBar
        _appBarLayoutState.value = expanded
    }

    //Função para atualizar a visibilidade do searchView
    //será chamada nos fragments
    fun setShowOrGoneSearchView(visibility: Int){
        _searchMenuState.value = visibility
    }

}