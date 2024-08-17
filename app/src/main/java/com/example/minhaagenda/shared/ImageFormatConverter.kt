package com.example.minhaagenda.shared

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import java.io.ByteArrayOutputStream
import java.util.Base64.Decoder

object ImageFormatConverter {
    fun imageToByteArray(image: Bitmap): ByteArray {
        return try {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            baos.toByteArray()
        } catch (e: Exception) {
            // Log de erro ou tratamento apropriado
            e.printStackTrace()
            ByteArray(0) // Retorna um array de bytes vazio em caso de falha
        }
    }

    //apenas para didática
    //função criada mas não será utilizada pois utilizarei o Glide
    private fun byteArrayToImage(byteArrayImage: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(byteArrayImage, 0, byteArrayImage.size)
        } catch (e: Exception) {
            // Log de erro ou tratamento apropriado
            e.printStackTrace()
            null // Retorna null em caso de falha
        }
    }


}