package com.example.minhaagenda.activities.main.fragments.importContacts

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.minhaagenda.database.ContactDatabase
import com.example.minhaagenda.entities.Contact
import java.io.File

class ImportContactsViewModel(application: Application) : AndroidViewModel(application){
    private var application = application
    private var _chooseContacts= MutableLiveData<ActivityResultLauncher<Intent>>()
    val chooseContacts:MutableLiveData<ActivityResultLauncher<Intent>> = _chooseContacts
    val repository = ContactDatabase.getContactDatabase(application).contactDAO()

    fun importFromCsv(uriContacts : Uri){
       var inputStreamCSV = application.contentResolver.openInputStream(uriContacts)
        var columnNameIndex= -1
        var columnPhoneIndex = -1
        var columnEmailIndex = -1

        try {
            inputStreamCSV?.let {stream->
                stream.bufferedReader().useLines { lines->
                   lines.forEachIndexed { index,line->
                       var arrayLine = if(line.contains(",")) line.split(",") else line.split(";")


                       if (index == 0){
                           columnNameIndex = arrayLine.indexOf("first_name")
                           columnPhoneIndex = arrayLine.indexOf("phone")
                           columnEmailIndex = arrayLine.indexOf("email")
                       }else {
                           if (columnNameIndex >= 0 && columnPhoneIndex >= 0) {
                               val contact = Contact()
                               contact.name = arrayLine[columnNameIndex]
                               contact.phone = arrayLine[columnPhoneIndex]
                               contact.email = arrayLine[columnEmailIndex]
                               repository.insertContact(contact)
                           }
                       }


                   }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    fun importFromVcf(uriContacts: Uri){
        var inputStreamCSV = application.contentResolver.openInputStream(uriContacts)
    }
}

class ImportContactsViewModelFactory(var application: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(ImportContactsViewModel::class.java)){
            return ImportContactsViewModel(application) as T
        }
        throw IllegalArgumentException("ViewModel Class Unknown")
    }
}