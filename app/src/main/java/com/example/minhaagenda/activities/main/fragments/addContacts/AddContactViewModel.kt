package com.example.minhaagenda.activities.main.fragments.addContacts

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.repositories.contact_repository.ContactRepository
import kotlinx.coroutines.launch

class AddContactViewModel(application: Application) : AndroidViewModel(application) {

    private val _bitmapContact = MutableLiveData<Bitmap>()
    val bitmapContact: MutableLiveData<Bitmap> get() = _bitmapContact

    private val _contactToUpdate = MutableLiveData<Contact>()
    val contactToUpdate: MutableLiveData<Contact> get () = _contactToUpdate

    private val repository = ContactRepository(application)

    /**
     * Adiciona ou atualiza um contato no repositório.
     *
     * @param contact O contato a ser adicionado ou atualizado.
     * @param onSuccess Função de callback a ser chamada quando a operação for bem-sucedida, recebendo o ID do contato salvo ou atualizado
     * @param onError Função de callback a ser chamada quando ocorrer um erro, recebendo a exceção gerada.
     */
    fun addOrUpdateContact(contact: Contact, onSuccess: (idSaved: Long) -> Unit, onError: (Exception) -> Unit) {
        // Inicia uma coroutine no escopo do ViewModel
        viewModelScope.launch {
            try {
                // Tenta adicionar ou atualizar o contato no repositório e obtém o ID salvo
                val id = saveContact(contact)

                // (Opcional) Adiciona um delay se precisar exibir um Snackbar ou outro feedback visual
                // delay(1000) // Exemplo de delay (se necessário)

                // Se a operação for bem-sucedida, chama a função onSuccess passando o ID do contato salvo
                onSuccess(id)
            } catch (e: Exception) {
                // Se ocorrer uma exceção durante a operação, chama a função onError passando a exceção
                onError(e)
            }
        }
    }

    /**
     * Determina se deve atualizar um contato existente ou inserir um novo.
     *
     * @param contact O contato a ser salvo.
     * @return O ID do contato salvo (seja inserido ou atualizado).
     */
    fun saveContact(contact: Contact): Long {
        return if (contact.id > 0) {
            // Atualiza o contato existente e retorna o ID do contato
            repository.updateContact(contact)
            return contact.id.toLong()
        } else {
            // Insere um novo contato e retorna o ID do novo contato(o metodo insertContact tem como retorno o id do contato salvo)
            repository.insertContact(contact)
        }
    }


    //metódo que valida os editTexts passados retornando um boleano para interromper o fluxo da aplicação se os mesmos estiverem em branco
    //estou utilizando varargs de EditText pois nem todos são MaskEditText(telefone é MaskEditText)
    fun isBlank(vararg editTexts: EditText):Boolean{
        var textIsBlank = false

        // Itera sobre cada EditText passado para a função
        editTexts.forEach {
            // Converte o conteúdo do EditText para uma string
            val text = it.text.toString()

            // Verifica se o texto está em branco
            if (text.isBlank()) {
                // Se o texto estiver em branco, exibe uma mensagem de erro abaixo do EditText
                it.error = "Preencha este campo!"
                textIsBlank = true
            }
        }
        return textIsBlank

    }

    //register para escolha de imagem(camera ou galeria)
     fun returnBitmap(result: ActivityResult) {
        //se galeria
        val uri = result.data?.data
        uri?.let {
            //implementação para converter a URI em um Bitmap de acordo com o sdk
            if (Build.VERSION.SDK_INT < 28) {
                _bitmapContact.value =
                    MediaStore.Images.Media.getBitmap(getApplication<Application>().contentResolver, it)

            } else {//se acima
                val source = ImageDecoder.createSource(getApplication<Application>().contentResolver, it)
                _bitmapContact.value = ImageDecoder.decodeBitmap(source)

            }
        }

        //se camera
        val data = result.data?.extras?.get("data")
        data?.let { image ->
            _bitmapContact.value = image as Bitmap
        }

    }


    fun toUpdate(contact: Contact){
        _contactToUpdate.value = contact
    }


}


// Fábrica de ViewModel para AllContactsViewModel
class AddContactViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    // Método para criar instâncias do ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // Verifica se a classe solicitada é AllContactsViewModel
        if (modelClass.isAssignableFrom(AddContactViewModel::class.java)) {
            // Retorna uma instância de AllContactsViewModel passando o Application como argumento
            @Suppress("UNCHECKED_CAST") //suprime avisos de conversões de tipo não verificáveis em tempo de execução quando você sabe que são seguras
            return AddContactViewModel(application) as T
        }
        // Lança uma exceção se a classe não for reconhecida
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}