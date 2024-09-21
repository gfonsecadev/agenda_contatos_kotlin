package com.example.minhaagenda.activities.showContact

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.minhaagenda.database.ContactDatabase
import com.example.minhaagenda.entities.Contact
import kotlinx.coroutines.launch

// ViewModel para gerenciar a lógica e os dados associados à ShowContactActivity
class ShowContactActivityViewModel(application: Application) : AndroidViewModel(application) {

    // MutableLiveData que armazena o contato atual. Pode ser observado para atualizações na UI.
    private val _contact = MutableLiveData<Contact>()
    // Propriedade pública que expõe o MutableLiveData, permitindo que a UI observe mudanças.
    val contact: MutableLiveData<Contact> = _contact


    // Repositório que acessa os dados do banco de dados via DAO.
    private val repositoryContact = ContactDatabase.getContactDatabase(application).contactDAO()

    /**
     * Função para buscar um contato pelo ID e atualizar o LiveData.
     * Essa função é executada em uma coroutine para operações assíncronas sem bloquear a UI.
     */
    fun getContact(id: Long) {
        viewModelScope.launch {
            // Atribui o contato recuperado ao _contact LiveData, que notificará os observadores.
            _contact.value = repositoryContact.getContact(id)
        }
    }

    /**
     * Função para deletar o contato atual.
     * Verifica se o contato existe e, se existir, o deleta do banco de dados.
     * A operação é executada em uma coroutine.
     */
    fun deleteContact() {
        viewModelScope.launch {
            // Verifica se _contact não é nulo antes de tentar deletar.
            _contact.value?.let {
                repositoryContact.deleteContact(it)
            }
        }
    }
}


class ShowContactActivityViewModelFactory(private val application: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(ShowContactActivityViewModel::class.java)){
            return ShowContactActivityViewModel(application) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}