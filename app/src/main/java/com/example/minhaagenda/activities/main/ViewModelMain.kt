package com.example.minhaagenda.activities.main

import android.app.Application
import android.os.Build
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.repositories.contact_repository.ContactRepository
import com.example.minhaagendakotlin.R
import kotlinx.coroutines.launch

class ViewModelMain(application: Application) : AndroidViewModel(application) {
     val repository_main = ContactRepository(application)

    fun dataDevice(): String {
        val brand = Build.BRAND.takeIf { it.isNotBlank() }?.replaceFirstChar { it.uppercase() } ?: "Unknown"
        val model = Build.MODEL.takeIf { it.isNotBlank() } ?: "Unknown"
        val dataDevice = "$brand $model"
        return dataDevice
    }


    //deleta todos os contatos selecionados
    fun deleteSelectedContacts(contacts:MutableSet<Contact>){
        contacts.forEach{
            viewModelScope.launch {
            repository_main.deleteContact(it)}
        }
    }
}

class ViewModelMainFactory(val application: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ViewModelMain::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ViewModelMain(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}