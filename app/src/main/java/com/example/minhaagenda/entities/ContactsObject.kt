package com.example.minhaagenda.entities
//classe passada para o adapter principal
data class ContactsObject(
    val letter: String,
    var contactList: List<Contact> = mutableListOf()
)


