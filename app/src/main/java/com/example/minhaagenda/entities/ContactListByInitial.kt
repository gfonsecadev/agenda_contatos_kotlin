package com.example.minhaagenda.entities
//classe passada para o adapter principal
data class ContactListByInitial(
    val letter: String,
    var contactList: List<Contact> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactListByInitial

        if (letter != other.letter) return false
        if (contactList != other.contactList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = letter.hashCode() *31
        result += contactList.hashCode()
        return result
    }


}


