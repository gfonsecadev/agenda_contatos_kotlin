package com.example.minhaagenda.shared

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

class LauncherSearchContacts {
    lateinit var launcherSearchContacts:ActivityResultLauncher<Intent>

    fun registerLauncher(register : ActivityResultLauncher<Intent>){
        launcherSearchContacts = register
    }

    fun searchByCsvFile(){
        val csvIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            setType("text/csv")
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        launcherSearchContacts.launch(csvIntent)
    }

    fun searchByVcfFile(){
        val vcfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            setType("text/x-vcard")
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        launcherSearchContacts.launch(vcfIntent)
    }
}