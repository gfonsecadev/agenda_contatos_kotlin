package com.example.minhaagenda.ui.fragments.allContacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.minhaagenda.entities.ContactsObject
import com.example.minhaagenda.mappers.ContactMapper
import com.example.minhaagenda.repositories.contact_repository.ContactRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AllContactsViewModel(application: Application) : AndroidViewModel(application) {
    var contactDatabase = ContactRepository(application).contactDatabase

    private var _listContactsObject = MutableLiveData<List<ContactsObject>>()
    val listContactsObject: MutableLiveData<List<ContactsObject>> get() = _listContactsObject


    fun getAllContacts(): Job {
       return viewModelScope.launch {
          var contacts = contactDatabase.getAllContact().sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER){it.name})
           _listContactsObject.value = ContactMapper.contactsListToAContactsObjectList(contacts)

       }
    }


}

// Fábrica de ViewModel para AllContactsViewModel
class AllContactsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    // Método para criar instâncias do ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // Verifica se a classe solicitada é AllContactsViewModel
        if (modelClass.isAssignableFrom(AllContactsViewModel::class.java)) {
            // Retorna uma instância de AllContactsViewModel passando o Application como argumento
            @Suppress("UNCHECKED_CAST") //suprime avisos de conversões de tipo não verificáveis em tempo de execução quando você sabe que são seguras
            return AllContactsViewModel(application) as T
        }
        // Lança uma exceção se a classe não for reconhecida
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}