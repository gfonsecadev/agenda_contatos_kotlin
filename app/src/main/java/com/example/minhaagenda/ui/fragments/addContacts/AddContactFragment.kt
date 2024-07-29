package com.example.minhaagenda.ui.fragments.addContacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.FragmentAddContactBinding

class AddContactFragment : Fragment() {
    private lateinit var binding: FragmentAddContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar o layout para este fragmento
        binding = FragmentAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         // Configura o ViewModel para gerenciar o estado do AppBarLayout
         setupViewModelAppBar()
         //Configurações ao clique na imagem do fragment
         setupImageContact()
         //Abre o teclado virtual para que o usuário possa começar a digitar.
         openKeyboard()


    }

    //Configura o comportamento da AppBar pela ViewModel
    fun setupViewModelAppBar(){
        //instãncia do AppBarViewModel
        val viewModelShare = ViewModelProvider(requireActivity()).get(AppBarViewModel::class.java)
        //chamamos o metodo setAppBarLayoutState para alterar o valor do MutableLiveData  e disparar o observer na actity passando o boleano
        viewModelShare.setAppBarLayoutState(false)//appBar não será exibida neste fragment
    }

    //Configuração do clique na imagem do contato, mudando o layout para escolha da imagem(galeria ou camera)
    fun setupImageContact(){
        //no clique da imagem retorno um layout que é inflado com uma animação de scale
        binding.imageChooseButton.setOnClickListener {
            // Inflar o layout adicional
            val layout = LayoutInflater.from(requireContext()).inflate(R.layout.choose_image_layout, binding.linearLayout, false)
            // Ocultar o botão de imagem
            binding.imageChooseButton.visibility = View.GONE
            // Adicionar o layout no container
            binding.linearLayout.addView(layout)
            // Adicionar animação ao layout
            layout.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.enter_layout_choose_image))
        }
    }

    //Metodo que abre o teclado no primeiro editText do fragment
    fun openKeyboard(){
        // Solicitar foco no primeiro EditText e mostrar o teclado
        binding.editName.requestFocus()
        binding.editName.postDelayed({
            val keyboard = requireActivity().getSystemService(InputMethodManager::class.java)
            keyboard?.showSoftInput(binding.editName, InputMethodManager.SHOW_IMPLICIT)
        }, 500) // Reduzir o atraso para 500 ms
    }
}
