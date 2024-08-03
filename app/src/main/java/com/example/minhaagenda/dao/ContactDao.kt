package com.example.minhaagenda.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.minhaagenda.entities.Contact

@Dao
interface ContactDao {
    @Insert
    fun insertContact(contact:Contact)

    @Update
    fun updateContact(contact:Contact)

    @Delete
    fun deleteContact(contact: Contact)

    @Query("SELECT * FROM Contato WHERE id = :id")
    fun getContact(id:Int):Contact

    @Query("SELECT * FROM Contato")
    fun getAllContact(): List<Contact>
}