package com.example.minhaagenda.shared

import android.content.Context
import com.example.minhaagenda.entities.Contact
import java.io.File
import java.io.FileWriter
import java.io.IOException

// Extensão para converter uma lista de contatos em um arquivo VCARD
fun List<Contact>.contactListToVcard(): File? {
    // Cria o arquivo temporário que será excluído automaticamente quando o sistema precisar liberar espaço ou quando o app for encerrado.
    val file = File.createTempFile("Contatos",".vcf")

    return try {
        // Usa o FileWriter com 'use' para garantir o fechamento do recurso
        FileWriter(file).use { fileWriter ->
            for (contact in this) {
                // Adiciona o formato padrão de um VCARD
                fileWriter.appendLine("BEGIN:VCARD")
                fileWriter.appendLine("VERSION:3.0")
                fileWriter.appendLine("FN:${contact.name}")  // Nome completo do contato
                fileWriter.appendLine("TEL:${contact.phone}")  // Número de telefone

                // Adiciona o email apenas se estiver preenchido
                if (contact.email.isNotBlank()) {
                    fileWriter.appendLine("EMAIL:${contact.email}")
                }

                // Finaliza o VCARD
                fileWriter.appendLine("END:VCARD")
                fileWriter.appendLine()  // Linha em branco entre contatos
            }
        }
        file  // Retorna o arquivo se tudo estiver certo
    } catch (e: IOException) {
        e.printStackTrace()  // Loga o erro
        null  // Retorna null em caso de erro
    }
}
