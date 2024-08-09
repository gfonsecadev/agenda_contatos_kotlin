package com.example.minhaagenda.activities.main.fragments.addContacts
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
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
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.shared.ImageFormatConverter
import com.example.minhaagenda.shared.LauncherPermissions
import com.example.minhaagenda.shared.LaunchersImage
import com.example.minhaagenda.shared.PermissionsManager
import com.example.minhaagenda.activities.showContact.ShowContactActivity
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.FragmentAddContactBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AddContactFragment() : Fragment() {
    private lateinit var binding: FragmentAddContactBinding
    private lateinit var layout:View
    private  lateinit var launcherPermissions: LauncherPermissions
    private  lateinit var launchersImage: LaunchersImage
    private val viewModelAddContact : AddContactViewModel by viewModels {  AddContactViewModelFactory(application = requireActivity().application) }
    private  var bitmapContactByteArray: ByteArray? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         // Configura o ViewModel para gerenciar o estado do AppBarLayout
        setupViewModelAppBar()
         //inicia os registerForActivityResult para permissôes e seleção de imagens(câmera e galeria) neste fragment
        registerLaunchersFragment()
        //metodo que dispara o launcher de permissões( infla um layout para escolha de imagens se permissões aceitas ou pede permissão novamente se as mesmas fora negadas
        setupImageContact()
         //Abre o teclado virtual para que o usuário possa começar a digitar.
         openKeyboard()
         saveContact()
         cancelSave()

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
                name = editName.text.toString()
                phone = editPhone.text.toString()
                email = editEmail.text.toString()
                image = bitmapContactByteArray // Supõe que bitmapContactByteArray seja uma variável existente que contém os bytes da imagem
            }

            // Chama o método addContact no ViewModel para adicionar o contato
            // onSuccess: Função de callback a ser chamada em caso de sucesso
            // onError: Função de callback a ser chamada em caso de erro
            viewModelAddContact.addContact(contact,
                onSuccess = { idSaved->
                    contactSaveSuccess(idSaved.toInt())
                },
                onError = {
                    contactSaveError(it.message.toString())
                }
            )
        }
    }

    private fun cancelSave(){
        binding.buttonCancel.setOnClickListener{
            goAllContactFragment()
        }
    }

    //sucesso ao salvar
    private fun contactSaveSuccess(contactId: Int) {
        //exibição do progress oculto
        binding.progressLayout.visibility = View.VISIBLE

        //coroutine para simular um carregamento para o progressBar através de um delay
        GlobalScope.launch(Dispatchers.Main) {
            delay(1000)
            //ocultação do progress após o delayc com exibição de um snackBar após
            binding.progressLayout.visibility = View.GONE
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

    //register para escolha de imagem(camera ou galeria)
    private fun takeImageFragment(){
        launchersImage = LaunchersImage()
        val intent = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {result->
            //contém a lógica de negocio
             viewModelAddContact.returnBitmap(result)

        }
        //atribui o register para a classe de ajuda (para poder ser lançada em outro momento)
        launchersImage.registerLauncher(intent)
        //observamos a mudança do liveData do bitmap
        observeBitmap()
        }

    //responsável por verificar mudanças no bitmap
    private fun observeBitmap(){
        viewModelAddContact.bitmapContact.observe(viewLifecycleOwner) { bitmap ->
            //se existit um bitmap
            bitmap?.let {
                binding.imageChooseButton.setImageBitmap(it)//imagem principal é setada com a imagem capturada pela camera
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

    private fun backPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                goAllContactFragment()
            }

        })
    }

    private fun goAllContactFragment(){
        startActivity(Intent(requireActivity(),AllContactsFragment::class.java))
    }
}
