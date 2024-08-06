package com.example.minhaagenda.mappers

import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactsObjetc

//criação de um mapper para conversão de contact para contactObject(esperado pelo adapter)
object ContactMapper {
    //função de conversão que recebe uma lista de contact e retorna uma lista de contactObject
    fun contactsListToAContactsObjectList(contactList: List<Contact>): List<ContactsObjetc> {
        val contactsObjetcLists:ArrayList<ContactsObjetc> = ArrayList<ContactsObjetc>()
        var contactsObjetc: ContactsObjetc?

        contactList.forEachIndexed { index, value ->

            if (index > 0 && (contactList[index - 1].name.getOrNull(0) !== value.name.getOrNull(0))) {
                contactsObjetc = ContactsObjetc()
                contactsObjetc!!.letter = contactList[index - 1].name.first().toString()
                contactsObjetc!!.contactList =
                    contactList.filter { it.name.startsWith(contactList[index - 1].name.first()) }
                contactsObjetcLists.add(contactsObjetc!!)
            }
            if (index == contactList.size - 1) {
                contactsObjetc = ContactsObjetc()
                contactsObjetc!!.letter = value.name.first().toString()
                contactsObjetc!!.contactList = contactList.filter { it.name.startsWith(value.name.first()) }
                contactsObjetcLists.add(contactsObjetc!!)
            }

        }

        return contactsObjetcLists
    }
}