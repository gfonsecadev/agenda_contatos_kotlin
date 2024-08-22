package com.example.minhaagenda.repositories.contact_repository

import android.content.Context
import com.example.minhaagenda.dao.ContactDao
import com.example.minhaagenda.database.ContactDatabase
import com.example.minhaagenda.entities.Contact

//padrão repository para encapsular o dominio da aplicação
class ContactRepository(context: Context) {
    //recuperação da instância do database criado retornando o dao
    var contactDatabase: ContactDao = ContactDatabase.getContactDatabase(context).contactDAO()

    fun insertContact(contact: Contact):Long{
        return contactDatabase.insertContact(contact)
    }

    fun getContactById(id: Long): Contact {
        return contactDatabase.getContact(id)
    }

    fun getContactByName(name: String): List<Contact>{
        return contactDatabase.getContactByName(name)
    }

    fun getAllContacts(): List<Contact> {
        return contactDatabase.getAllContact()
    }

    fun updateContact(contact: Contact){
        return contactDatabase.updateContact(contact)
    }

    fun deleteContact(contact: Contact){
        return contactDatabase.deleteContact(contact)
    }

}