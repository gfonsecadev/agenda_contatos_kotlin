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

class ShowContactActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val _contact = MutableLiveData<Contact>()
    val contact:MutableLiveData<Contact> = _contact

    private val repositoryContact = ContactDatabase.getContactDatabase(application).contactDAO()

    fun getContact(id: Int){
        viewModelScope.launch {
            _contact.value = repositoryContact.getContact(id)
        }
    }
}

class ShowContactActivityViewModelFactory(private val application: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ShowContactActivityViewModel::class.java)){
            return ShowContactActivityViewModel(application) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}