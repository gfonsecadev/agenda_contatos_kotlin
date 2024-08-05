package com.example.minhaagenda.ui.fragments.addContacts

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
import com.santalu.maskara.widget.MaskEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddContactViewModel(application: Application) : AndroidViewModel(application) {

    private val _bitmapContact = MutableLiveData<Bitmap>()
    val bitmapContact: MutableLiveData<Bitmap> get() = _bitmapContact


    private val repository = ContactRepository(application)

    fun addContact(contact: Contact, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        // Inicia uma coroutine no escopo do ViewModel
        viewModelScope.launch {
            try {
                // Tenta inserir o contato no repositório
                repository.insertContact(contact)
                //delay para exibir o Snackbar
                // Se bem-sucedido, chama a função onSuccess
                onSuccess()
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


//para utilizar parametros em view model é necessário criar uma factory
class AddContactViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(AddContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return AddContactViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}