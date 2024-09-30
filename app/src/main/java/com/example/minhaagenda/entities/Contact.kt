package com.example.minhaagenda.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Contato")
class Contact() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo("nome")
    lateinit var name:String

    @ColumnInfo("numero")
    lateinit var phone: String

    @ColumnInfo("email")
    lateinit var email: String

    @ColumnInfo("imagem")
    var image: ByteArray? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt().toLong()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
        email = parcel.readString().toString()
        image = parcel.createByteArray()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as Contact

        return  id == other.id &&
                name == other.name &&
                phone == other.phone &&
                email == other.email &&
                image.contentEquals(other.image)

    }


    override fun hashCode(): Int {
        var result = id.hashCode()
        result += 31 * name.hashCode()
        result += 31 * phone.hashCode()
        result += 31 * email.hashCode()
        result += 31 * image.contentHashCode()
        return result
    }

}


