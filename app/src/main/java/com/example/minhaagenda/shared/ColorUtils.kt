package com.example.minhaagenda.shared
// ColorUtils.kt
import android.graphics.Color

//função utilitária para criar uma cor aleatória
fun randomColor(): Int {
    // Gerando cores r, g, b aleatórias.
    val red = (0..255).random()
    val green = (0..255).random()
    val blue = (0..255).random()
    return Color.rgb(red, green, blue)
}