package com.example.minhaagenda.shared

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.TelephonyManager
import android.text.Editable
import android.widget.Toast

// Extensão para converter uma String em Editable
fun String.stringToEditable(): Editable {
    return Editable.Factory.getInstance().newEditable(this)
}

// Extensão para obter a primeira letra de uma String
fun String.firstLetter(): String {
    return this.first().toString()
}

// Extensão para remover a máscara do número( parênteses e hífens)
fun String.onlyNumbers(): String {
    return this.replace("[-()\\s]".toRegex(), "")
}

// Extensão para abrir o WhatsApp com um número de telefone específico
fun String.openWhatsApp(context: Context) {
    val numberWithCodeCountry = getCountryCode(context) + this

    val intent = Intent(Intent.ACTION_VIEW).apply {
        // Define a URL para iniciar uma conversa no WhatsApp com o número fornecido
        data = Uri.parse("https://wa.me/$numberWithCodeCountry")
        // Garante que o Intent seja direcionado para o WhatsApp, se estiver instalado
        setPackage("com.whatsapp")
    }

    // Verifica se há algum aplicativo que pode lidar com o Intent
    val activities = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

    if (activities.isNotEmpty()) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Nenhum aplicativo pode lidar com essa ação.", Toast.LENGTH_SHORT).show()
    }
}

// Extensão para realizar uma chamada telefônica
fun String.callContact(context: Context) {
    //se for um numero com 11 posições ou seja se contiver o ddd + número eu adiciono o codigo de longa distância da operadora
    val number = if(this.matches("\\d{11}".toRegex())) {
        getOperatorCode(context) + this
    }else {//senão retorno o número como está
            this
        }


    val intent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$number")
    }
    context.startActivity(intent)
}

// Extensão para abrir o app de mensagens com um número pré-preenchido
fun String.messageContact(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:${this@messageContact}")
    }

    val applications = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

    if (applications.isNotEmpty()) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Nenhum aplicativo de mensagens encontrado.", Toast.LENGTH_SHORT).show()
    }
}

// Função para obter o TelephonyManager do sistema
private fun getTelephonyManager(context: Context): TelephonyManager {
    return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
}

// Função para obter o código do país com base no ISO do país
private fun getCountryCode(context: Context): String {
    val code = getTelephonyManager(context).networkCountryIso.uppercase()
    return code.getCountryCode()
}

// Função para obter o código da operadora de acordo com o país
private fun getOperatorCode(context: Context): String? {
    val telephonyManager = getTelephonyManager(context)
    val code = telephonyManager.simOperatorName.uppercase()
    return code.getOperatorCode(telephonyManager.networkCountryIso.uppercase())
}

// Função para mapear o código de discagem dos principais paises
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
        // Adicionei apenas alguns países
    )
    return countryCodes[this] ?: "1" // Retorna "1" como padrão se o país não for encontrado
}

// Função para mapear o código das operadoras de acordo com o país
fun String.getOperatorCode(codeCountry: String): String? {
    val operatorCodes = when (codeCountry) {
        "BR" -> mapOf(
            "VIVO" to "015",
            "CLARO" to "021",
            "TIM" to "041",
            "OI" to "031",
            "NEXTEL" to "99"
            // Adicionei as principais operadoras brasileiras
        )
        // Adicionar outros países aqui se necessário

        else -> emptyMap() // Caso o país não seja reconhecido, retorna um mapa vazio
    }

    return operatorCodes[this]
}
