package com.example.minhaagenda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//view model para exibição ou recolhimento da AppBar
class SharedViewModel : ViewModel() {

    //essa variavel que sofrerá mudançãs através do método setAppBarLayoutState
    private val _appBarLayoutState = MutableLiveData<Boolean>()

    //essa variavel que será observada na activity
    // sempre será chamada quando a variavel _appBarLayoutState sofrer alteração através do metodo setAppBarLayoutState
    val appBarLayoutState: LiveData<Boolean> get() = _appBarLayoutState

    // Função para atualizar o estado do AppBarLayout
    //será chamada nos fragments
    fun setAppBarLayoutState(expanded: Boolean) {
        //valor passado vai alterar a variavel abaixo disparando assim o observador(appBarLayoutState) na activity
        //dentro deste observer na activity que contém a logica para fechar a AppBar
        _appBarLayoutState.value = expanded
    }
}