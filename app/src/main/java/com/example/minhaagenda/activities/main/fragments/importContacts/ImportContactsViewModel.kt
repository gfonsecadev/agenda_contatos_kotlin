package com.example.minhaagenda.activities.main.fragments.importContacts

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.minhaagenda.database.ContactDatabase
import com.example.minhaagenda.entities.Contact

class ImportContactsViewModel(application: Application) : AndroidViewModel(application){
    private var application = application
    private val repository = ContactDatabase.getContactDatabase(application).contactDAO()

    fun importFromCsv(uriContacts : Uri){
       val inputStreamCSV = application.contentResolver.openInputStream(uriContacts)
        var columnNameIndex= -1
        var columnPhoneIndex = -1
        var columnEmailIndex = -1
        var separator = ""

        try {
            inputStreamCSV?.let {stream->
                stream.bufferedReader().useLines { lines->
                   lines.forEachIndexed { index,line->

                       if (index == 0){
                           separator = if(line.contains(",")) "," else ";"
                           val arrayLine = manualReplaceCommas(line)
                           columnNameIndex = arrayLine.indexOf("first_name")
                           columnPhoneIndex = arrayLine.indexOf("phone")
                           columnEmailIndex = arrayLine.indexOf("email")
                       }else {
                           if (columnNameIndex >= 0 && columnPhoneIndex >= 0) {

                               val arrayLine = manualReplaceCommas(line)
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


    fun regexReplaceCommas(line: String): List<String> {
        // Regex que encontra vírgulas fora das aspas
        val regex = Regex(""",(?=(?:[^"]*"[^"]*")*[^"]*$)""")

        // Substitui as vírgulas fora das aspas por asteriscos
        val replaced = regex.replace(line, "*")

        // Divide a string resultante pelos asteriscos e retorna a lista
        return replaced.split("*")
    }


    fun manualReplaceCommas(line: String): List<String> {
        var isQuote = false  // Indica se o caractere atual está dentro de aspas
        val replaced = StringBuilder()  // StringBuilder para construir a string resultante

        // Percorre cada caractere na linha com o índice
        line.forEachIndexed { index, char ->
            if (char == '"') {
                // Alterna o estado de estar dentro ou fora de aspas
                isQuote = !isQuote
            }

            if (isQuote) {
                // Se dentro de aspas, adiciona o caractere diretamente
                replaced.append(char)
            } else {
                // Se fora de aspas
                if (char == ',') {
                    // Substitui vírgulas fora de aspas por asteriscos
                    replaced.append('*')
                } else {
                    // Adiciona outros caracteres diretamente
                    replaced.append(char)
                }
            }
        }

        // Divide a string resultante pelos asteriscos e retorna a lista
        return replaced.toString().split("*")
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