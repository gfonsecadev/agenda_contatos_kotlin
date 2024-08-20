package com.example.minhaagenda.activities.showContact

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.minhaagenda.activities.main.MainActivity
import com.example.minhaagenda.activities.main.fragments.addContacts.AddContactFragment
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.shared.contactListToVcard
import com.example.minhaagenda.shared.exportContact
import com.example.minhaagenda.shared.firstLetter
import com.example.minhaagenda.shared.onlyNumbers
import com.example.minhaagenda.shared.openWhatsApp
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

        //Configura todos os listeners desta activity
        buttonListeners()
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
                    Glide.with(this).load(it).into(binding.showContactImage)
                } ?: run {
                    binding.showContactLetter.text = contact.name.firstLetter()//extension criada para exibir a primeira letra
                }

                // Exibe os dados do contato na UI
                binding.showTextName.text = contact.name
                binding.showTextPhone.text = contact.phone

                // se não houver email o layout que abriga o textView é Ocultado
                if (contact.email.isBlank()) {
                    binding.showLayoutTextEmail.visibility = View.INVISIBLE
                } else {//senão o textView recebe um valor
                    binding.showLayoutTextEmail.visibility = View.VISIBLE
                    binding.showTextEmail.text = contact.email
                }
            }
        }
    }

    // Configura os listeners para os botões da interface do usuário
    private fun buttonListeners() {
        // Configura um listener para o botão de compartilhamento
        binding.showWhatsapp.setOnClickListener {
            // utiliza a extension onlyNumbers que criei para formatar o número de telefone recebido removendo todos os caracteres não numéricos
            val formattedPhoneNumber = contactReceived.phone.onlyNumbers()
            // Abre o WhatsApp com o número de telefone formatado utilizando a extension openWhatsApp que criei
            formattedPhoneNumber.openWhatsApp(baseContext)
        }

        binding.clickShare.setOnClickListener {
            //converto o contato para lista para utilizar a extension que criei para converter o contatos para formato Vcard
            val contactToListFile = listOf(contactReceived).contactListToVcard()
            contactToListFile?.let {
                //função criada para exportar arquivo de contatos por uma intent
                exportContact(it, baseContext)
            }

        }
    }

    // Configura o comportamento ao pressionar o botão de voltar
    private fun backPressed() {
        // Adiciona um callback para o evento de pressionar o botão de voltar
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            // Método chamado quando o botão de voltar é pressionado
            override fun handleOnBackPressed() {
                // Inicia a MainActivity
                startActivity(Intent(baseContext, MainActivity::class.java))

                // Finaliza a Activity atual
                finish()
            }
        })
    }



}
