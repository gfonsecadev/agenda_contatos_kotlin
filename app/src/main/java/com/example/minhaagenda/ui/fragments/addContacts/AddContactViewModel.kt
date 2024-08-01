package com.example.minhaagenda.ui.fragments.addContacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.minhaagenda.database.ContactDatabase
import com.example.minhaagenda.entities.Contact

class AddContactViewModel(application: Application) : AndroidViewModel(application) {
    private val _contact = MutableLiveData<Contact>()
    val contact: MutableLiveData<Contact> get() = _contact

    private val database = ContactDatabase.getContactDatabase(application)

    fun addContact(contact: Contact){
        database.contactDAO().insertContact(contact)
    }

    fun buildContact(){

    }

}