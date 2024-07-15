package com.example.minhaagenda.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Contato")
class Contact() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo("nome")
    lateinit var name:String

    @ColumnInfo("numero")
    var number: Int = 0
}
