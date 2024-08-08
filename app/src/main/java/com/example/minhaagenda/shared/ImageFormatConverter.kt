package com.example.minhaagenda.shared

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import java.io.ByteArrayOutputStream
import java.util.Base64.Decoder

object ImageFormatConverter {
    fun imageToByteArray(image: Bitmap) : ByteArray{
        var baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 30, baos)
        return baos.toByteArray()
    }

    fun byteArrayToImage(byteArrayImage: ByteArray) : Bitmap{
        return BitmapFactory.decodeByteArray(byteArrayImage,0,byteArrayImage.size)
    }

}