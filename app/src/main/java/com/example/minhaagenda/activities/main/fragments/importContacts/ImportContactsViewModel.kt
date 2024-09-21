package com.example.minhaagenda.activities.main.fragments.importContacts

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.minhaagenda.database.ContactDatabase
import com.example.minhaagenda.entities.Contact

// ViewModel responsável por importar contatos a partir de arquivos CSV e VCF.
class ImportContactsViewModel(application: Application) : AndroidViewModel(application) {
    // Instância da aplicação para acessar recursos e o sistema de arquivos
    private var application = application

    // Instancia o repositório do banco de dados de contatos
    private val repository = ContactDatabase.getContactDatabase(application).contactDAO()

    // Função que importa contatos a partir de um arquivo CSV localizado em 'uriContacts'
    fun importFromCsv(uriContacts: Uri): Boolean {
        // Abre o InputStream para ler o arquivo CSV
        val inputStreamCSV = application.contentResolver.openInputStream(uriContacts)
        var columnNameIndex = -1
        var columnPhoneIndex = -1
        var columnEmailIndex = -1
        var separator = ' '

        try {
            // Verifica se o InputStream não é nulo
            inputStreamCSV?.let { stream ->
                // Usa o BufferedReader para ler as linhas do CSV
                stream.bufferedReader().useLines { lines ->
                    // Itera sobre cada linha do arquivo CSV
                    lines.forEachIndexed { index, line ->
                        if (index == 0) {
                            // Define o separador (vírgula ou ponto e vírgula) para o arquivo CSV
                            separator = if (line.contains(",")) ',' else  ';'

                            // Substitui vírgulas fora das aspas
                            val arrayLine = manualReplaceCommas(line,separator)

                            // Encontra o índice das colunas "first_name", "phone" e "email"
                            columnNameIndex = arrayLine.indexOf("first_name")
                            columnPhoneIndex = arrayLine.indexOf("phone")
                            columnEmailIndex = arrayLine.indexOf("email")
                        } else {
                            // Verifica se os índices de coluna são válidos
                            if (columnNameIndex >= 0 && columnPhoneIndex >= 0) {
                                // Divide a linha usando a função manualReplaceCommas
                                val arrayLine = manualReplaceCommas(line,separator)

                                // Cria um novo contato e preenche os dados
                                val contact = Contact()
                                contact.name = arrayLine[columnNameIndex]
                                contact.phone = arrayLine[columnPhoneIndex]
                                contact.email = arrayLine[columnEmailIndex]

                                // Insere o contato no banco de dados
                                repository.insertContact(contact)
                            }
                        }
                    }
                }
            }
            return true
        } catch (e: Exception) {
            // Log de erro para debugar
            e.printStackTrace()
            return false
        }
    }

    // Função que substitui vírgulas fora de aspas usando Regex
    fun regexReplaceCommas(line: String): List<String> {
        // Regex que encontra vírgulas fora das aspas
        val regex = Regex(""",(?=(?:[^"]*"[^"]*")*[^"]*$)""")

        // Substitui as vírgulas fora das aspas por asteriscos
        val replaced = regex.replace(line, "*")

        // Divide a string resultante pelos asteriscos e retorna a lista
        return replaced.split("*")
    }

    // Função que manualmente substitui vírgulas fora de aspas
    private fun manualReplaceCommas(line: String, separator: Char): List<String> {
        var isQuote = false  // Indica se o caractere atual está dentro de aspas
        val replaced = StringBuilder()  // StringBuilder para construir a string resultante

        // Percorre cada caractere na linha
        line.forEachIndexed { index, char ->
            if (char == '"') {
                // Alterna o estado de estar dentro ou fora de aspas
                isQuote = !isQuote
            }

            if (isQuote) {
                // Se dentro de aspas, adiciona o caractere diretamente
                replaced.append(char)
            } else {
                // Se fora de aspas
                if (char == separator) {
                    // Substitui separador fora de aspas por asteriscos
                    replaced.append('*')
                } else {
                    // Adiciona outros caracteres diretamente
                    replaced.append(char)
                }
            }
        }

        // Divide a string resultante pelos asteriscos e retorna a lista
        return replaced.toString().split("*")
    }

    // Função que importa contatos a partir de um arquivo VCF
    fun importFromVcf(uriContacts: Uri): Boolean {
        // Abre o InputStream para ler o arquivo VCF
        val inputStreamVcf = application.contentResolver.openInputStream(uriContacts)

        try {
            // Verifica se o InputStream não é nulo
            inputStreamVcf?.let { stream ->
                // Usa o BufferedReader para ler as linhas do VCF
                stream.bufferedReader().useLines { lines ->
                    // Concatena todas as linhas para facilitar a análise de cada contato
                    val text = lines.reduce { acc, line -> "$acc\n$line" }

                    // Divide o texto em múltiplos contatos usando o delimitador "BEGIN:VCARD"
                    val contacts = text.split("BEGIN:VCARD")
                    contacts.forEach { contact ->
                        // Extrai o nome, telefone e email usando Regex
                        val name = "FN.*:(.*)".toRegex().find(contact)?.groupValues?.get(1)?.trim()
                        val phone = "TEL.*:(.*)".toRegex().find(contact)?.groupValues?.get(1)?.trim()
                        val email = "EMAIL.*:(.*)".toRegex().find(contact)?.groupValues?.get(1)?.trim()

                        // Verifica se os campos obrigatórios estão preenchidos
                        if (!name.isNullOrEmpty() && !phone.isNullOrEmpty()) {
                            val contactSave = Contact()
                            contactSave.name = name
                            contactSave.phone = phone
                            contactSave.email = email.orEmpty()

                            // Insere o contato no banco de dados
                            repository.insertContact(contactSave)
                        }
                    }
                }
            }
            return true
        } catch (e: Exception) {
            // Log de erro para debugar
            e.printStackTrace()
            return false
        }
    }
}

// Factory para criar a instância do ViewModel
class ImportContactsViewModelFactory(var application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(ImportContactsViewModel::class.java)) {
            return ImportContactsViewModel(application) as T
        }
        throw IllegalArgumentException("ViewModel Class Unknown")
    }
}
