package com.example.minhaagenda.ui.addContacts


import android.os.Bundle
import android.view.CollapsibleActionView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.FragmentAddContactBinding
import com.google.android.material.appbar.CollapsingToolbarLayout


class AddContactFragment : Fragment() {
 lateinit var binding:FragmentAddContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddContactBinding.inflate(inflater)
        //recupero o collapsingLayout para o ocultar.
        val collapsingBar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingBar?.visibility= View.GONE

        //no clique da imagem retorno um layout que é inflado com uma animação de scale
        binding.imageChooseButton.setOnClickListener {
            //recupero o layout
            val layout = inflater.inflate(R.layout.choose_image_layout,binding.linearLayout,false)
            //oculto a imagem para o layout ser exibido sozinho no container
            binding.imageChooseButton.visibility = View.GONE
            //adiciono no container o meu layout
            binding.linearLayout.addView(layout)
            //adiciono a animação que o layout terá ao ser chamado
            layout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.enter_layout_choose_image))

        }

        //solicito focu no primeiro editText
        binding.editName.requestFocus()
        //logo depois chamo a instãncia do gerenciador do teclado para mostrar o mesmo(teclado)
        binding.editName.postDelayed({
            //recupero o gerenciador
            val keybord = activity?.getSystemService(android.app.Service.INPUT_METHOD_SERVICE) as InputMethodManager
            //metodo do gerenciador para mostrar o teclado com foco no editText Passado
            keybord.showSoftInput(binding.editName,InputMethodManager.SHOW_IMPLICIT)
        },10)

        return binding.root

    }

}