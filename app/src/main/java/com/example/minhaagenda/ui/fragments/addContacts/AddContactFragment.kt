package com.example.minhaagenda.ui.fragments.addContacts

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.shared.LaunchersViewModel
import com.example.minhaagenda.shared.Permissions
import com.example.minhaagenda.shared.PermissionsViewModel
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.FragmentAddContactBinding

class AddContactFragment : Fragment() {
    private lateinit var binding: FragmentAddContactBinding
    private lateinit var layout:View
    private  val permissionsViewModel: PermissionsViewModel by viewModels()
    private  val launchersViewModel: LaunchersViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initializeViewModelPermissions()
         // Configura o ViewModel para gerenciar o estado do AppBarLayout
        setupViewModelAppBar()
         //Configurações ao clique na imagem do fragment
        observeAndHandleRegisters()
        setupImageContact()
         //Abre o teclado virtual para que o usuário possa começar a digitar.
         openKeyboard()

    }

    fun observeAndHandleRegisters(){
        registerAndObservePermissions()
        registerAndObserveLaucherGallery()
    }

    // Inicializa os ViewModels
    fun registerAndObservePermissions() {
          var permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
              result->
               var isGranted = result.all { it.value }
                  permissionsViewModel.changeLiveData(isGranted)
              }

          permissionsViewModel.registerLauncherPermissions(permissions)

          permissionsViewModel.isPermissionGranted.observe(viewLifecycleOwner){
              isGranted->
              //se tiver as permissôes o clique na imagem infla o layuot para escolha da imagem
              Permissions.executeIfPermissionGranted(isGranted, onPermissionGranted = { inflateLayout() }, requireActivity())
          }

        }

    fun registerAndObserveLaucherGallery(){
        val intent = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            // Verifica se o resultado foi OK e se os dados não são nulos
            if (result.resultCode == RESULT_OK) {

                result.data?.data?.let { uri ->
                    // Atualiza o ViewModel com o URI da imagem selecionada
                    launchersViewModel.changeLiveData(uri)
                }
                
            }

        }
        launchersViewModel.registerLauncher(intent)
        launchersViewModel.uri.observe(viewLifecycleOwner) { uri ->
           uri.let {
                binding.imageChooseButton.setImageURI(uri)
                binding.cardViewImage.visibility = View.VISIBLE
                layout.visibility = View.GONE
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
                         permissionsViewModel.askPermissions()}
    }

    private fun inflateLayout(){
        // Inflar o layout adicional
        layout = LayoutInflater.from(requireContext()).inflate(R.layout.choose_image_layout, binding.layoutImageAddContact, false)
        // Ocultar o botão de imagem
        binding.cardViewImage.visibility = View.GONE
        // Adicionar o layout no container
        binding.layoutImageAddContact.addView(layout)
        // Adicionar animação ao layout
        layout.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.enter_layout_choose_image))
        buttonsClickListeners()
    }

    private fun buttonsClickListeners(){
        val buttonGalery = layout.findViewById<Button>(R.id.choose_galery)
        val buttonCamera = layout.findViewById<Button>(R.id.choose_camera)
        buttonGalery.setOnClickListener{
            launchersViewModel.openGallery()
        }

        buttonCamera.setOnClickListener{
            launchersViewModel.openCamera()
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
}
