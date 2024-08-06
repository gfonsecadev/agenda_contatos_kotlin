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
    lateinit var phone: String

    @ColumnInfo("email")
    lateinit var email: String

    @ColumnInfo("imagem")
    var image: ByteArray? = null
}
