package com.example.minhaagenda.mappers

import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactObjetc

//criação de um mapper para conversão de contact para contactObject(esperado pelo adapter)
object ContactMapper {
    //função de conversão que recebe uma lista de contact e retorna uma lista de contactObject
    fun contactListToAContactObjectList(contactList: ArrayList<Contact>): List<ContactObjetc> {
        var contactObjetcList:ArrayList<ContactObjetc> = ArrayList<ContactObjetc>()
        var contactObjetc: ContactObjetc?

        contactList.forEachIndexed { index, value ->

            if (index > 0 && (contactList[index - 1].name.first() !== value.name.first())) {
                contactObjetc = ContactObjetc()
                contactObjetc!!.letter = contactList[index - 1].name.first().toString()
                contactObjetc!!.contactList =
                    contactList.filter { it.name.startsWith(contactList[index - 1].name.first()) }
                contactObjetcList.add(contactObjetc!!)
            }
            if (index == contactList.size - 1) {
                contactObjetc = ContactObjetc()
                contactObjetc!!.letter = value.name.first().toString()
                contactObjetc!!.contactList = contactList.filter { it.name.startsWith(value.name.first()) }
                contactObjetcList.add(contactObjetc!!)
            }

        }

        return contactObjetcList
    }
}