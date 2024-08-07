package com.example.minhaagenda.mappers

import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactListByInitial

//criação de um mapper para conversão de contact para contactObject(esperado pelo adapter)
object ContactMapper {
    //função de conversão que recebe uma lista de contact e retorna uma lista de contactObject
    fun contactsListToAContactsObjectList(contactList: List<Contact>): List<ContactListByInitial> {
        if (contactList.isEmpty()) return emptyList()

        return contactList
            .groupBy { it.name.first().uppercaseChar() }
            .map { (letter, contacts) -> ContactListByInitial(letter.toString(), contacts) }
    }

    /*  old method --> click here to show
    fun contactsListToAContactsObjectList(contactList: List<Contact>): List<ContactListByInitial> {
        val contactsObjectList = mutableListOf<ContactListByInitial>()

        if (contactList.isEmpty()) return contactsObjectList

        var currentLetter = contactList.first().name.first().uppercaseChar()
        var currentContactList = mutableListOf<Contact>()

        for (contact in contactList) {
            if (contact.name.first().uppercaseChar() == currentLetter) {
                currentContactList.add(contact)
            } else {
                contactsObjectList.add(ContactListByInitial(currentLetter.toString(), currentContactList))
                currentLetter = contact.name.first().uppercaseChar()
                currentContactList = mutableListOf(contact)
            }
        }

        // Adiciona o último grupo
        contactsObjectList.add(ContactListByInitial(currentLetter.toString(), currentContactList))

        return contactsObjectList
    } */

}