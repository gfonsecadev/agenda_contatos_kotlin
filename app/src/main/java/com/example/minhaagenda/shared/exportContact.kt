package com.example.minhaagenda.shared

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File


fun exportContact(file: File, context: Context) {
    try {
        // Obtém um URI seguro e compatível com FileProvider para o arquivo de contatos
        val fileProviderUri = FileProvider.getUriForFile(context, "${context.packageName}.provider",file )// packageName é usado para criar o authority correto file


            // Cria uma Intent para enviar o arquivo de contatos usando o tipo MIME adequado
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, fileProviderUri) // Anexa o URI do arquivo à Intent
                type = "text/x-vcard" // Define o tipo MIME para arquivos de vCard (contatos)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // Concede permissão de leitura ao app de destino
            }

            // Inicia um seletor de aplicativos para que o usuário escolha qual app usar para compartilhar o arquivo
            context.startActivity(Intent.createChooser(intent, "Escolha um aplicativo para exportar seus contatos:"))

    } catch (e: IllegalArgumentException) {
        // Captura e imprime qualquer exceção IllegalArgumentException que possa ocorrer
        e.printStackTrace()
    }
}


