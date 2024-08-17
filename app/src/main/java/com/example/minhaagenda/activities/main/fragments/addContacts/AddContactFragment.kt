package com.example.minhaagenda.activities.main.fragments.addContacts
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.minhaagenda.activities.main.MainActivity
import com.example.minhaagenda.activities.showContact.ShowContactActivity
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.shared.ImageFormatConverter
import com.example.minhaagenda.shared.LauncherPermissions
import com.example.minhaagenda.shared.LaunchersImage
import com.example.minhaagenda.shared.PermissionsManager
import com.example.minhaagenda.shared.stringToEditable
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.FragmentAddContactBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AddContactFragment : Fragment() {
    private lateinit var binding: FragmentAddContactBinding
    private lateinit var layout:View
    private  lateinit var launcherPermissions: LauncherPermissions
    private  lateinit var launchersImage: LaunchersImage
    private val viewModelAddContact : AddContactViewModel by viewModels {  AddContactViewModelFactory(application = requireActivity().application) }
    private  var bitmapContactByteArray: ByteArray? = null
    private lateinit var contactToUpdate: Contact


    /**
     * Método chamado quando o fragmento é criado.
     * Recupera os argumentos e configura o ViewModel.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recupera os argumentos do Bundle e configura o ViewModel
        arguments?.let { bundle ->
            val contact = getCorrectParcelable(bundle)
            contact?.let {
                viewModelAddContact.toUpdate(it)
            }
        }
    }

    /**
     * Recupera um objeto [Contact] do [Bundle] com compatibilidade para diferentes versões do Android.
     */
    private fun getCorrectParcelable(bundle: Bundle): Contact? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable("contactToUpdate", Contact::class.java)
        } else {
            bundle.getParcelable("contactToUpdate")
        }
    }

    /**
     * Infla o layout do fragmento e retorna a raiz da visão.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Configura a interface do usuário e gerencia eventos após a criação da visão.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura o ViewModel para gerenciar o estado do AppBarLayout
        setupViewModelAppBar()
        // Inicializa os lançadores para permissões e seleção de imagens
        registerLaunchersFragment()
        // Configura o layout para escolha de imagens e permissões
        setupImageContact()
        // Abre o teclado virtual para entrada de dados
        openKeyboard()
        // Configura o listener para salvar ou atualizar um contato
        saveContact()
        //Configura o listener para deixar o fragment
        cancelSave()
        //Configura  a ação ao clicar no botão de voltar do dispositivo
        backPressed()
    }



    //metodo que salva contatos
    private fun saveContact() {
        // Configura um clique listener para o botão de salvar
        binding.buttonSave.setOnClickListener {
            // Obtém as referências para os campos de entrada
            val editName = binding.editName
            val editPhone = binding.editPhone
            val editEmail = binding.editEmail

            // Verifica se os campos editName e editPhone estão em branco
            if (viewModelAddContact.isBlank(editName, editPhone)) {
                // Se algum campo estiver em branco, retorna do clique listener
                return@setOnClickListener
            }


            // Cria uma nova instância de Contact e atribui os valores dos campos
            val contact = Contact().apply {
                id = if (::contactToUpdate.isInitialized) contactToUpdate.id else 0
                name = editName.text.toString()
                phone = editPhone.text.toString()
                email = editEmail.text.toString()
                image = bitmapContactByteArray // Supõe que bitmapContactByteArray seja uma variável existente que contém os bytes da imagem
            }

            // Chama o método addContact no ViewModel para adicionar o contato
            // onSuccess: Função de callback a ser chamada em caso de sucesso
            // onError: Função de callback a ser chamada em caso de erro
            viewModelAddContact.addOrUpdateContact(contact,
                onSuccess = { idSaved->
                    contactSaveSuccess(idSaved)
                },
                onError = {
                    contactSaveError(it.message.toString())
                }
            )
        }
    }

    private fun cancelSave() {
        // Configura um listener para o botão "Cancelar"
        binding.buttonCancel.setOnClickListener {
            // Quando o botão é clicado, chama o método goAllContactFragment que retorna ao fragment que mostra todos os contatos
            goAllContactFragment()
        }
    }


    //sucesso ao salvar
    private fun contactSaveSuccess(contactId: Long) {
        //exibição do progress oculto
        binding.progressSaveContact.progressLayout.visibility = View.VISIBLE

        //coroutine para simular um carregamento para o progressBar através de um delay
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1000)
            //ocultação do progress após o delayc com exibição de um snackBar após
            binding.progressSaveContact.progressLayout.visibility = View.GONE
            Snackbar.make(binding.root, "Contato salvo com sucesso!", Snackbar.LENGTH_SHORT).show()

            val intent = Intent(requireActivity(), ShowContactActivity::class.java)
            intent.putExtra("contact_id", contactId)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    //Erro ao salvar
    private fun contactSaveError(error:String){
        // Mostra uma mensagem de erro usando Toast
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }


    //inicia os registerForActivityResult para permissôes e seleção de imagens(câmera e galeria)
    private fun registerLaunchersFragment(){
        hasPermissionsFragment()
        takeImageFragment()
        hasDataToUpdateFragment()
    }

    //metodo para lidar quando o fragment recebe argumentos(se receber significa que se trata de edição)
    private fun hasDataToUpdateFragment() {
        // Observa o LiveData 'contactToUpdate' no ViewModel, atrelando o ciclo de vida ao do Fragmento.
        viewModelAddContact.contactToUpdate.observe(viewLifecycleOwner) { contact ->

            // Verifica se 'contact' não é nulo. Se for nulo, o código abaixo não será executado.
            if (contact != null) {
                //inicio os objetos globalmente indicando que a partir daqui se trata de edição e não adição de contatos
                contactToUpdate = contact
                bitmapContactByteArray = contact.image

                // Utiliza o método 'apply' para configurar múltiplos componentes de UI relacionados ao binding.
                binding.apply {
                    // Atualiza o campo 'editName' com o nome do contato, convertendo para Editable, através da extensão criada(stringToEditable)
                    editName.text = contact.name.stringToEditable()
                    // Atualiza o campo 'editPhone' com o telefone do contato, convertendo para Editable, através da extensão criada(stringToEditable)
                    editPhone.text = contact.phone.stringToEditable()
                    // Atualiza o campo 'editEmail' com o email do contato, convertendo para Editable, através da extensão criada(stringToEditable)
                    editEmail.text = contact.email.stringToEditable()
                }

                // Verifica se o contato tem uma imagem associada. Se 'image' não for nulo, executa o bloco 'let'.
                contact.image?.let {
                    // Lança uma coroutine no 'lifecycleScope', garantindo que o código seja executado na Main Thread.
                    lifecycleScope.launch(Dispatchers.Main) {
                        // Converte o array de bytes (image) para Bitmap e define como imagem do botão 'imageChooseButton'.
                        Glide.with(requireActivity()).load(it).into(binding.imageChooseButton)
                    }
                }
            }
        }
    }



    //register para pedir permissão
    private fun hasPermissionsFragment() {
        launcherPermissions = LauncherPermissions()
          val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
              result->
               val isGranted = result.all { it.value }
              //metodo que se permissão dada executa uma função (neste caso aqui infla o layout de escolha de imagem se não pede permissôes novamente)
              PermissionsManager.executeIfPermissionGranted(isGranted, onPermissionGranted = { inflateLayout() }, requireActivity())
          }

        //atribui o register para a classe de ajuda (para poder ser lançada em outro momento)
        launcherPermissions.registerLauncherPermissions(permissions)


        }

    // Função para iniciar o processo de escolha de imagem, seja da câmera ou da galeria
    private fun takeImageFragment() {
        // Inicializa um launcher para o resultado da atividade
        launchersImage = LaunchersImage()

        // Registra um ActivityResultLauncher com o contrato StartActivityForResult
        // Para iniciar uma atividade que retorna um resultado
        val intent = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // Este bloco de código é chamado quando a atividade retorna um resultado

            // Verifica se o código de resultado indica que a operação foi bem-sucedida
            if (result.resultCode == Activity.RESULT_OK) {
                // Inicia uma coroutine na Main Thread para garantir que a UI seja atualizada corretamente
                lifecycleScope.launch(Dispatchers.Main) {
                    // Torna o ProgressBar visível para indicar que uma operação está em andamento
                    binding.progressSaveContact.progressLayout.visibility = View.VISIBLE

                    // Adiciona um pequeno atraso (300ms) para garantir que o ProgressBar seja exibido
                    delay(300)

                    // Chama a função viewModelAddContact.returnBitmap() para processar o resultado da imagem
                    viewModelAddContact.returnBitmap(result)

                    // Torna o ProgressBar invisível após o processamento da imagem
                    binding.progressSaveContact.progressLayout.visibility = View.GONE
                }
            }
        }

        // Atribui o ActivityResultLauncher à classe de ajuda para que possa ser iniciado em outro momento
        launchersImage.registerLauncher(intent)

        // Observa as mudanças no LiveData do bitmap para atualizar a UI conforme necessário
        observeBitmap()
    }


    //responsável por verificar mudanças no bitmap
    private fun observeBitmap(){
        viewModelAddContact.bitmapContact.observe(viewLifecycleOwner) { bitmap ->
            //se existit um bitmap
            bitmap?.let {
                Glide.with(requireActivity()).load(it).into(binding.imageChooseButton) //imagem principal é setada com a imagem capturada pela camera
                //layout principal volta a ser exibido e layout de escolha das imagens é ocultado
                binding.cardViewImage.visibility = View.VISIBLE
                layout.visibility = View.GONE
                bitmapContactByteArray = ImageFormatConverter.imageToByteArray(it)
            }
        }
    }

    //Configura o comportamento da AppBar pela ViewModel
    private fun setupViewModelAppBar(){
        //instãncia do AppBarViewModel
        val viewModelShare = ViewModelProvider(requireActivity()).get(AppBarViewModel::class.java)
        //chamamos o metodo setAppBarLayoutState para alterar o valor do MutableLiveData  e disparar o observer na actity passando o boleano
        viewModelShare.setAppBarLayoutState(false)//appBar não será exibida neste fragment
    }

    //Configuração do clique na imagem do contato, mudando o layout para escolha da imagem(galeria ou camera)
    private fun setupImageContact(){
        binding.imageChooseButton.setOnClickListener{
                         launcherPermissions.askPermissions()}
    }

    //infla o layout para escolha da imagem(galeria ou camera)
    private fun inflateLayout(){
        // Inflar o layout adicional
        layout = LayoutInflater.from(requireContext()).inflate(R.layout.choose_image_layout, binding.layoutImageAddContact, false)
        // Ocultar a imagem principal para dar lugar ao layout
        binding.cardViewImage.visibility = View.GONE
        // Adicionar o layout no container
        binding.layoutImageAddContact.addView(layout)
        // Adicionar animação de entrada ao layout
        layout.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.enter_layout_choose_image))
        buttonsClickListeners()//ações dos listeners do layout
    }

    //listeners dos botôes do layout inflado
    private fun buttonsClickListeners(){
        //recuperando as referencias dos botôes do layout
        val buttonGallery = layout.findViewById<Button>(R.id.choose_galery)
        val buttonCamera = layout.findViewById<Button>(R.id.choose_camera)

        //se galeria, o launcher da galeria da classe de ajuda é disparada
        buttonGallery.setOnClickListener{
            launchersImage.openGallery()
        }

        //se camera o launcher da camera da classe de ajuda é disparada
        buttonCamera.setOnClickListener{
            launchersImage.openCamera()
        }

    }



    //Metodo que abre o teclado no primeiro editText do fragment
    private fun openKeyboard(){
        // Solicitar foco no primeiro EditText e mostrar o teclado
        binding.editName.requestFocus()
        binding.editName.postDelayed({
            val keyboard = requireActivity().getSystemService(InputMethodManager::class.java)
            keyboard?.showSoftInput(binding.editName, InputMethodManager.SHOW_IMPLICIT)
        }, 500) // Reduzir o atraso para 500 ms
    }

    // Método que configura o comportamento ao pressionar o botão "voltar"
    private fun backPressed() {
        // Adiciona um callback ao OnBackPressedDispatcher da atividade
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            // Método chamado quando o botão "voltar" é pressionado
            override fun handleOnBackPressed() {
                // Chama o método goAllContactFragment para navegação
                goAllContactFragment()
            }
        })
    }

    // Método para navegar para o fragmento "AllContactsFragment"
    private fun goAllContactFragment() {
        // Verifica se a atividade atual é uma instância de MainActivity
        if (requireActivity() is MainActivity) {
            // Cast para MainActivity e chama o método para alterar o fragmento
            (requireActivity() as MainActivity).changeFragmentNavController(R.id.nav_all, R.id.nav_item_all)
        } else {
            // Se a atividade atual não for MainActivity, inicia uma nova instância de MainActivity
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            // Finaliza a atividade atual para evitar empilhamento
            requireActivity().finish()
        }
    }




}
