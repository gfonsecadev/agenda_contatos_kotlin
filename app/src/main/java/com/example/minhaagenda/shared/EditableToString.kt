package com.example.minhaagenda.shared

import android.text.Editable

object EditableToString {
    fun editableToString(editable: String):Editable{
        return Editable.Factory.getInstance().newEditable(editable)
    }
}