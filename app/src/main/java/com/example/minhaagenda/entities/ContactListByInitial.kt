package com.example.minhaagenda.entities
//classe passada para o adapter principal
data class ContactListByInitial(
    val letter: String,
    var contactList: List<Contact> = mutableListOf()
)


