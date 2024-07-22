package com.example.minhaagenda.ui.addContacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.minhaagenda.MainActivity
import com.example.minhaagenda.SharedViewModel
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

        //instãncia do SharedViewModel
        val viewModelShare = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        //chamamos o metodo setAppBarLayoutState para alterar o valor do MutableLiveData  e disparar o observer na actity passando o boleano
        viewModelShare.setAppBarLayoutState(false)//appBar não será exibida neste fragment

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

        // Solicitar foco no primeiro EditText e mostrar o teclado
        binding.editName.requestFocus()
        binding.editName.postDelayed({
            val keyboard = requireActivity().getSystemService(InputMethodManager::class.java)
            keyboard?.showSoftInput(binding.editName, InputMethodManager.SHOW_IMPLICIT)
        }, 500) // Reduzir o atraso para 500 ms
    }
}
