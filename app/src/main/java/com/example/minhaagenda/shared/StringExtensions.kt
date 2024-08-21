package com.example.minhaagenda.shared

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.TelephonyManager
import android.text.Editable
import android.widget.Toast

fun String.stringToEditable():Editable{
        return Editable.Factory.getInstance().newEditable(this)
    }

   fun String.firstLetter():String{
       return this.first().toString()
   }

fun String.onlyNumbers(): String {
    return this.replace("[()-]".toRegex(),"")
}

// Extensão para abrir o WhatsApp com um número de telefone específico
fun String.openWhatsApp(context: Context) {
    val numberWithCodeCountry = getCountryCode(context) + this

    // Cria um Intent para abrir o WhatsApp com uma URL formatada
    val intent = Intent(Intent.ACTION_VIEW).apply {
        // Define a URL para iniciar uma conversa no WhatsApp com o número fornecido
        data = Uri.parse("https://wa.me/$numberWithCodeCountry")
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
    val number:String
    if (this.contains("[^0-9]".toRegex())){
        number = getOperatorCode(context) + this
    }else{
        number = this
    }

    val intent =Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:${number}")
    }
    context.startActivity(intent)

}

fun String.messageContact(context: Context){
    val intent = Intent(Intent.ACTION_SENDTO)
   
    intent.apply {
        data = Uri.parse("smsto:${this@messageContact}")
    }

    val applications = context.packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY)

    if (applications.isNotEmpty()) {
        context.startActivity(intent)
    } else {
        // Opção para lidar com a ausência de um app de mensagens instalado
        Toast.makeText(context, "Nenhum aplicativo de mensagens encontrado.", Toast.LENGTH_SHORT).show()
    }

}

private fun getTelephonyManager(context: Context): TelephonyManager {
    return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
}

private fun getCountryCode(context: Context): String {
    val code = getTelephonyManager(context).getNetworkCountryIso().uppercase()
    return code.getCountryCode()
}

private fun getOperatorCode(context: Context): String? {
    val telephonyManager = getTelephonyManager(context)
    val code = telephonyManager.simOperatorName.uppercase()
    return code.getOperatorCode(telephonyManager.getNetworkCountryIso().uppercase())
}


fun String.getCountryCode(): String {
    val countryCodes = mapOf(
        "US" to "1",   // Estados Unidos
        "CN" to "86",  // China
        "IN" to "91",  // Índia
        "BR" to "55",  // Brasil
        "RU" to "7",   // Rússia
        "ID" to "62",  // Indonésia
        "PK" to "92",  // Paquistão
        "NG" to "234", // Nigéria
        "BD" to "880", // Bangladesh
        "JP" to "81"   // Japão
        //escolhi somente alguns paises
    )
    return countryCodes[this] ?: "1" // Retorna "1" como padrão se não encontrado
}

fun String.getOperatorCode(codeCountry:String): String? {
    val operatorCodes = when(codeCountry){
        "BR"-> mapOf(
            "VIVO" to "015",
            "CLARO" to "021",
            "TIM" to "041",
            "OI" to "031",
            "NEXTEL" to "99"
            // Adicionei as principais operadoras brasileiras
        )
        //...deixei para futuras adições de outros paises.

        else-> emptyMap()//
    }

    return operatorCodes[this]
}






