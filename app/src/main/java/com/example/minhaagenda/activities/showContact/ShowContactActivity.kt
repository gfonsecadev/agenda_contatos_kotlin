package com.example.minhaagenda.activities.showContact

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.minhaagenda.activities.main.MainActivity
import com.example.minhaagenda.activities.main.fragments.addContacts.AddContactFragment
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.shared.ImageFormatConverter
import com.example.minhaagenda.shared.firstLetter
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.ActivityShowContactBinding

/**
 * Activity responsável por exibir os detalhes de um contato específico.
 */
class ShowContactActivity : AppCompatActivity() {

    // Variável para acessar o layout binding
    private lateinit var binding: ActivityShowContactBinding

    // ViewModel para gerenciar o estado e a lógica da Activity
    private val viewModelShowContact: ShowContactActivityViewModel by viewModels {
        ShowContactActivityViewModelFactory(application)
    }

    // Variável para armazenar o contato recebido
    private lateinit var contactReceived: Contact

    /**
     * Método chamado quando a Activity é criada.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout usando View Binding
        binding = ActivityShowContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera o ID do contato da intent
        receiveIdContact()

        // Observa mudanças no ViewModel
        observeViewModel()

        // Configura os cliques nos botões
        setupClickListeners()

        //Comportamento do botão de voltar do dispositivo
        backPressed()
    }

    /**
     * Configura os listeners para os cliques nos botões.
     */
    private fun setupClickListeners() {
        // Listener para o botão de compartilhamento
        binding.clickShare.setOnClickListener {
            Toast.makeText(this, "Compartilhar", Toast.LENGTH_SHORT).show()
        }

        // Listener para o botão de edição
        binding.clickEdit.setOnClickListener {
            callAddFragment()
        }

        // Listener para o botão de exclusão
        binding.clickDelete.setOnClickListener {
            viewModelShowContact.deleteContact()
            finish() // Fecha a Activity após a exclusão do contato
        }
    }

    /**
     * Chama o fragmento de adição/edição de contato, passando o contato atual como argumento.
     */
    private fun callAddFragment() {
        //argumentos contendo dos dados do contato para edição
        val bundleContact = bundleOf("contactToUpdate" to contactReceived)

        // Inicia a transição do fragmento com animação
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.container, AddContactFragment::class.java, bundleContact)
        fragmentTransition.setCustomAnimations(R.anim.enter_fragment, R.anim.close_fragment)
        fragmentTransition.commit()
    }

    /**
     * Recupera o ID do contato da intent que iniciou a Activity.
     */
    private fun receiveIdContact() {
        val contactId = intent.getLongExtra("contact_id", -1)
        if (contactId > 0) {
            //dispara o metodo que atualiza o LiveData
            viewModelShowContact.getContact(contactId)
        }
    }

    /**
     * Observa as mudanças no ViewModel e atualiza a UI com os dados do contato.
     */
    private fun observeViewModel() {
        viewModelShowContact.contact.observe(this) { contact ->
            if (contact != null) {
                contactReceived = contact

                // Se o contato tiver uma imagem, converte e exibe na ImageView
                contact.image?.let {
                    binding.showContactImage.setImageBitmap(ImageFormatConverter.byteArrayToImage(it))
                } ?: run {
                    binding.showContactLetter.text = contact.name.firstLetter()//extension criada para exibir a primeira letra
                }

                // Exibe os dados do contato na UI
                binding.showTextName.text = contact.name
                binding.showTextPhone.text = contact.phone

                // Esconde o campo de email se estiver em branco
                if (contact.email.isBlank()) {
                    binding.showTextEmail.visibility = View.GONE
                } else {
                    binding.showTextEmail.text = contact.email
                }
            }
        }
    }


    fun backPressed(){
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                startActivity(Intent(baseContext,MainActivity::class.java))
                finish()
            }

        })
    }


}
