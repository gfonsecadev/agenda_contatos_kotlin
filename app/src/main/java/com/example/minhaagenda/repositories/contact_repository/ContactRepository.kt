package com.example.minhaagenda.repositories.contact_repository

import android.content.Context
import com.example.minhaagenda.dao.ContactDao
import com.example.minhaagenda.database.ContactDatabase
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.mappers.ContactMapper

//padrão repository para encapsular o dominio da aplicação
class ContactRepository(context: Context) {
    //recuperação da instância do database criado retornando o dao
    var contactDatabase: ContactDao = ContactDatabase.getContactDatabase(context).contactDAO()


    fun insertContact(contact: Contact){
        return contactDatabase.insertContact(contact)
    }

    suspend fun getContact(id: Int){
        return contactDatabase.getContact(id)
    }

    suspend fun getAllContacts(): List<Contact> {
        return contactDatabase.getAllContact()

    }

    suspend fun updateContact(contact: Contact){
        return contactDatabase.updateContact(contact)
    }

    suspend fun deleteContact(contact: Contact){
        return contactDatabase.deleteContact(contact)
    }

}