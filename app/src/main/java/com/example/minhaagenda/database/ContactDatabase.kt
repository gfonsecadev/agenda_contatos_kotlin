package com.example.minhaagenda.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.minhaagenda.dao.ContactDao
import com.example.minhaagenda.entities.Contact

//esta anotação recebe a(as) classe(es) que representam o BD e a versão do BD
@Database(entities = [Contact::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {//classe que extende de RoomDatabase
    
    abstract fun contactDAO(): ContactDao

    //para ser utilizado como estático
    companion object{
        @Volatile
        private lateinit var INSTANCE: ContactDatabase

        //metodo singleton para garantir uma unica instância da classe
         fun getContactDatabase(context: Context): ContactDatabase {
             //Os :: em Kotlin são usados para acessar referências a membros de classe ou funções
            if (!::INSTANCE.isInitialized) {
                //Ao usar synchronized, você está garantindo que apenas uma thread por vez execute o bloco de código dentro do synchronized
                synchronized(ContactDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ContactDatabase::class.java,
                        "contact_database")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }

}