package com.example.minhaagenda.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity("Contato")
class Contact() : Parcelable {
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

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
        email = parcel.readString().toString()
        image = parcel.createByteArray()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeByteArray(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}
