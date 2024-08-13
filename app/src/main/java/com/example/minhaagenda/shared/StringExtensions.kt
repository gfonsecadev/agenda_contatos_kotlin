package com.example.minhaagenda.shared

import android.text.Editable

    fun String.stringToEditable():Editable{
        return Editable.Factory.getInstance().newEditable(this)
    }

   fun String.firstLetter():String{
       return this.first().toString()
   }

