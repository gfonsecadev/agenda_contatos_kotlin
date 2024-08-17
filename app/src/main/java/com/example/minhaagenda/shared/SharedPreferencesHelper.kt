package com.example.minhaagenda.shared

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import android.util.Base64

class SharedPreferencesHelper(context: Context) {
    private var preferences: SharedPreferences = context.getSharedPreferences("Profile", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()

    // Função que recebe um Bitmap, o converte para uma string em Base64 e o armazena em SharedPreferences.
    fun editPreferencesImage(bitmap: Bitmap) {
        //utilizo o helper criado para me retornar um byteArray
        val baos = ImageFormatConverter.imageToByteArray(bitmap)
        // Converte o array de bytes do ByteArrayOutputStream para uma string Base64.
        // Isso permite que a imagem seja armazenada como texto em SharedPreferences.
        val imageString = Base64.encodeToString(baos, Base64.DEFAULT)

        // Armazena a string Base64 da imagem no SharedPreferences usando a chave "imageProfile".
        // `editor` é um SharedPreferences.Editor já inicializado.
        editor.putString("imageProfile", imageString)

        // Salva as mudanças feitas no SharedPreferences. O método commit() aplica as mudanças de forma síncrona.
        editor.commit()
    }


    fun editPreferencesName(name:String){
        editor.putString("nameProfile",name)
        editor.commit()
    }

    //recupera a imagem
    fun getPreferencesImage(): ByteArray {
        // Recupera a string Base64 armazenada em SharedPreferences com a chave "imageProfile".
        // Caso não haja uma imagem armazenada, retorna uma string vazia por padrão.
        val image = preferences.getString("imageProfile", "")

        // Converte a string Base64 de volta para um array de bytes.
        // Se 'image' for uma string vazia, 'imageProfile' será um array de bytes vazio.
        return Base64.decode(image, Base64.DEFAULT)
    }


    fun getPreferencesName(): String? {
        val name = preferences.getString("nameProfile","Your name")
        return name
    }
}