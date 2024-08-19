package com.example.minhaagenda.shared
// ColorUtils.kt
import android.graphics.Color

// Função para criar uma cor aleatória baseada em um nome
fun randomColor(name: String): Int {
    // Gera um valor hash único do nome
    val hashName = name.hashCode()

    // Isola e extrai o componente vermelho da cor
    // Máscara 0xff0000 seleciona apenas os bits da cor vermelha
    // Desloca 16 bits à direita para alinhar o valor (0-255)
    val red = (hashName and 0xff0000) shr(16)

    // Isola e extrai o componente verde da cor
    // Máscara 0x00ff00 seleciona apenas os bits da cor verde
    // Desloca 8 bits à direita para alinhar o valor (0-255)
    val green = (hashName and 0x00ff00) shr(8)

    // Isola e extrai o componente azul da cor
    // Máscara 0x0000FF seleciona apenas os bits da cor azul (já na posição correta)
    val blue = (hashName and 0x0000FF)

    // Combina os componentes RGB em uma cor e retorna
    return Color.rgb(red, green, blue)
}
