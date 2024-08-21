package com.example.minhaagenda.shared

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.Editable
import android.widget.Toast

fun String.stringToEditable():Editable{
        return Editable.Factory.getInstance().newEditable(this)
    }

   fun String.firstLetter():String{
       return this.first().toString()
   }

fun String.onlyNumbers(): String {
    return this.replace("[^\\d]".toRegex(),"")
}

// Extensão para abrir o WhatsApp com um número de telefone específico
fun String.openWhatsApp(context: Context) {
    // Cria um Intent para abrir o WhatsApp com uma URL formatada
    val intent = Intent(Intent.ACTION_VIEW).apply {
        // Define a URL para iniciar uma conversa no WhatsApp com o número fornecido
        data = Uri.parse("https://wa.me/55$this") // Substitua '55' pelo código do país e 'this' pelo número de telefone
        // Garante que o Intent seja direcionado para o WhatsApp, se estiver instalado
        setPackage("com.whatsapp")
    }

    // Consulta o PackageManager para verificar quais aplicativos podem lidar com o Intent criado
    val activities = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

    // Verifica se há pelo menos uma atividade que pode lidar com o Intent
    if (activities.isNotEmpty()) {
        // Se houver atividades, inicia o Intent para abrir o WhatsApp
        context.startActivity(intent)
    } else {
        // Se nenhuma atividade puder lidar com o Intent, mostra uma mensagem de erro
        Toast.makeText(context, "Nenhum aplicativo pode lidar com essa ação.", Toast.LENGTH_SHORT).show()
    }
}

fun String.callContact(context: Context){
    val intent =Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$this")
    }
    context.startActivity(intent)

}


