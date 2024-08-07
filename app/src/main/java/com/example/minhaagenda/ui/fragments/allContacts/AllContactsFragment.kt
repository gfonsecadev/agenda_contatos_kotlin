package com.example.minhaagenda.ui.fragments.allContacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.adapters.ContactAdapter
import com.example.minhaagenda.animations.fade.FadeToViews.fadeInImmediately
import com.example.minhaagenda.animations.fade.FadeToViews.fadeOut
import com.example.minhaagenda.entities.ContactListByInitial
import com.example.minhaagendakotlin.databinding.FragmentAllContactsBinding


class AllContactsFragment : Fragment() {

    private val viewModelAllContacts : AllContactsViewModel by viewModels() { AllContactsViewModelFactory(requireActivity().application) }
    private lateinit var binding: FragmentAllContactsBinding
    private lateinit var listContactListByInitial:List<ContactListByInitial>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllContactsBinding.inflate(inflater)

        // Configura o ViewModel para gerenciar o estado do AppBarLayout
        setupViewModelAppBar()
        //Configura o observador do viewModel
        setupViewModelAllContacts()
        //metodo com toda configuração do recyclerView
        //recyclerSettings(listContactListByInitial)

        return binding.root

    }

    private  fun setupViewModelAllContacts(){
        viewModelAllContacts.getAllContacts()
        viewModelAllContacts.listContactListByInitial.observe(viewLifecycleOwner) {
                listContactListByInitial = it
                recyclerSettings(listContactListByInitial)
        }
    }

    //Configura o comportamento da AppBar pela ViewModel
    private fun setupViewModelAppBar() {
        //instãncia do AppBarViewModel
        val viewModelShare = ViewModelProvider(requireActivity()).get(AppBarViewModel::class.java)
        //chamamos o metodo setAppBarLayoutState para alterar o valor do MutableLiveData  e disparar o observer na actity passando o boleano
        viewModelShare.setAppBarLayoutState(true)//appBar não será exibida neste fragment
    }

    //configuração do recyclerView Principal
    private fun recyclerSettings(list: List<ContactListByInitial>){
        //o contactAdapter recebe uma lista de contact onde o mapper contactToContactObject converte para contactObjeto que é o exigido pelo adapter
        val adapter = ContactAdapter(list, context)

        binding.recyclerContact.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerContact.adapter = adapter

       //utilizo o scrollListener para ouvir mudanças no scroll do recyclerView
        binding.recyclerContact.addOnScrollListener(object: OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //recupero o layoutManager
                val layout = recyclerView.layoutManager as LinearLayoutManager
                //recupero o primeiro item visivel do recyclerView
                //o metódo getItem foi implementado por min no atual adapter para retornar o item da lista de acordo com a posição passada
                val item = adapter.getItem(layout.findFirstVisibleItemPosition())
                //passo a letra do contactObject deste item
                binding.textLetter.text = item.letter

                if(newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING){
                    fadeInImmediately(binding.textLetter)
                }else{
                    fadeOut(binding.textLetter,500)
                }

            }
        })
        
    }


}


