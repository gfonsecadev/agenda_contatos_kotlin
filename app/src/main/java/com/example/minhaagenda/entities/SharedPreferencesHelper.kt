package com.example.minhaagenda.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import android.util.Base64

class SharedPreferencesHelper(context: Context) {
    var context = context
    var preferences = context.getSharedPreferences("Profile", Context.MODE_PRIVATE)
    var editor = preferences.edit()

    // Função que recebe um Bitmap, o converte para uma string em Base64 e o armazena em SharedPreferences.
    fun editPreferencesImage(bitmap: Bitmap) {
        // Cria um ByteArrayOutputStream para armazenar os bytes do Bitmap comprimido.
        val baos = ByteArrayOutputStream()

        // Comprime o Bitmap no formato JPEG com uma qualidade de 50%.
        // A compressão reduz a qualidade da imagem para diminuir o tamanho do arquivo.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)

        // Converte o array de bytes do ByteArrayOutputStream para uma string Base64.
        // Isso permite que a imagem seja armazenada como texto em SharedPreferences.
        val imageString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

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
    fun getPreferencesImage(): Bitmap? {
        // Recupera a string Base64 armazenada em SharedPreferences com a chave "imageProfile".
        // Caso não haja uma imagem armazenada, retorna uma string vazia por padrão.
        val image = preferences.getString("imageProfile", "")

        // Converte a string Base64 de volta para um array de bytes.
        // Se 'image' for uma string vazia, 'imageProfile' será um array de bytes vazio.
        val imageProfile = Base64.decode(image, Base64.DEFAULT)

        // Decodifica o array de bytes em um objeto Bitmap.
        // Se a decodificação falhar (por exemplo, se o array de bytes estiver vazio ou corrompido),
        // 'decodeByteArray' retornará null.
        return BitmapFactory.decodeByteArray(imageProfile, 0, imageProfile.size)
    }


    fun getPreferencesName(): String? {
        val name = preferences.getString("nameProfile","Your name")
        return name
    }
}