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
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsViewModel
import com.santalu.maskara.widget.MaskEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddContactViewModel(application: Application) : AndroidViewModel(application) {

    private val _bitmapContact = MutableLiveData<Bitmap>()
    val bitmapContact: MutableLiveData<Bitmap> get() = _bitmapContact


    private val repository = ContactRepository(application)

    fun addContact(contact: Contact, onSuccess: (idSaved: Long) -> Unit, onError: (Exception) -> Unit) {
        // Inicia uma coroutine no escopo do ViewModel
        viewModelScope.launch {
            try {
                // Tenta inserir o contato no repositório
                val id = repository.insertContact(contact)
                //delay para exibir o Snackbar
                // Se bem-sucedido, chama a função onSuccess
                onSuccess(id)
            } catch (e: Exception) {
                // Se ocorrer uma exceção, chama a função onError passando a exceção
                onError(e)
            }
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