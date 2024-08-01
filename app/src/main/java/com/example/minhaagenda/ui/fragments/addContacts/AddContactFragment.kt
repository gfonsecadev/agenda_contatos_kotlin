package com.example.minhaagenda.ui.fragments.addContacts

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.shared.LaunchersImage
import com.example.minhaagenda.shared.PermissionsManager
import com.example.minhaagenda.shared.LauncherPermissions
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.FragmentAddContactBinding

class AddContactFragment : Fragment() {
    private lateinit var binding: FragmentAddContactBinding
    private lateinit var layout:View
    private  lateinit var launcherPermissions: LauncherPermissions
    private  lateinit var launchersImage: LaunchersImage
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
            //se galeria
            val url = result.data?.data
            url?.let {uri->
                binding.imageChooseButton.setImageURI(uri)//imagem principal é setada com o uri da imagem escolhida

            }

            //se camera
            val data = result.data?.extras?.get("data")
            data?.let { image->
                val bitmap = image as Bitmap
                binding.imageChooseButton.setImageBitmap(bitmap)//imagem principal é setada com a imagem capturada pela camera
            }
            //layout principal volta a ser exibido e layout de escolha das imagens é ocultado
            binding.cardViewImage.visibility = View.VISIBLE
            layout.visibility = View.GONE
        }
        //atribui o register para a classe de ajuda (para poder ser lançada em outro momento)
        launchersImage.registerLauncher(intent)

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
}
