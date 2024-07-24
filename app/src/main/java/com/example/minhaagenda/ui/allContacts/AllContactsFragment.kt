package com.example.minhaagenda.ui.allContacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.minhaagenda.SharedViewModel
import com.example.minhaagenda.adapters.ContactAdapter
import com.example.minhaagenda.animations.fade.FadeToViews.fadeInImmediately
import com.example.minhaagenda.animations.fade.FadeToViews.fadeOut
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactObjetc
import com.example.minhaagenda.mappers.ContactMapper.contactListToAContactObjectList
import com.example.minhaagendakotlin.databinding.FragmentAllContactsBinding


class AllContactsFragment : Fragment() {
    var listAux: ArrayList<ContactObjetc> = ArrayList<ContactObjetc>()

    private lateinit var binding: FragmentAllContactsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllContactsBinding.inflate(inflater)

        //instãncia do SharedViewModel
        val viewModelShare = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        //chamamos o metodo setAppBarLayoutState para alterar o valor do MutableLiveData  e disparar o observer na actity passando o boleano
        viewModelShare.setAppBarLayoutState(true)//appBar será exibida neste fragment


        var list = ArrayList<Contact>()
        var contact = Contact()
        contact.name = "BRUNO"
        list.add(contact)
        contact = Contact()
        contact.name = "ALICIA"
        list.add(contact)
        contact = Contact()
        contact.name = "ARLO"
        list.add(contact)
        contact = Contact()
        contact.name = "ATENCIO"
        list.add(contact)
        contact = Contact()
        contact.name = "ALBERTO"
        list.add(contact)
        contact = Contact()
        contact.name = "EVERTON"
        list.add(contact)
        contact = Contact()
        contact.name = "ERICA"
        list.add(contact)
        contact = Contact()
        contact.name = "Gilmar"
        list.add(contact)
        contact = Contact()
        contact.name = "PEDRO"
        list.add(contact)
        contact = Contact()
        contact.name = "QUEREN"
        list.add(contact)
        contact = Contact()
        contact.name = "ADRIANO"
        list.add(contact)
        contact = Contact()
        contact.name = "CARLO"
        list.add(contact)
        contact = Contact()
        contact.name = "CATIA"
        list.add(contact)
        contact = Contact()
        contact.name = "DENILSON"
        list.add(contact)
        contact = Contact()
        contact.name = "DAVID"
        list.add(contact)

        list.sortBy { it.name }

        //metodo com toda configuração do recyclerView
        recyclerSettings(list)

        return binding.root


    }

    //configuração do recyclerView Principal
    fun recyclerSettings(list: ArrayList<Contact>){
        //o contactAdapter recebe uma lista de contact onde o mapper contactToContactObject converte para contactObjeto que é o exigido pelo adapter
        val adapter = ContactAdapter(contactListToAContactObjectList(list), context)

        binding.recyclerContact.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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


