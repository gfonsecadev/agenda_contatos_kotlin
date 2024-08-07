package com.example.minhaagenda.mappers

import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactsObject

//criação de um mapper para conversão de contact para contactObject(esperado pelo adapter)
object ContactMapper {
    //função de conversão que recebe uma lista de contact e retorna uma lista de contactObject
    fun contactsListToAContactsObjectList(contactList: List<Contact>): List<ContactsObject> {
        if (contactList.isEmpty()) return emptyList()
        
        return contactList
            .groupBy { it.name.first().uppercaseChar() }
            .map { (letter, contacts) -> ContactsObject(letter.toString(), contacts) }
    }

}