package com.example.minhaagenda.ui.allContacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minhaagenda.adapters.ContactAdapter
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactObjetc
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.FragmentAllContactsBinding
import com.google.android.material.appbar.CollapsingToolbarLayout


class AllContactsFragment : Fragment() {
    var listAux: ArrayList<ContactObjetc> = ArrayList<ContactObjetc>()

    private lateinit var binding: FragmentAllContactsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllContactsBinding.inflate(inflater)
        //seto a visibilidade do collapsingBar para visivél quando for este fragment(para exibir os contatos)
        val collapseBar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapseBar?.visibility= View.VISIBLE

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
        val adapter = ContactAdapter(contactToArrayContactObject(list), context)
        binding.recyclerContact.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerContact.adapter = adapter

        return binding.root


    }

    fun contactToArrayContactObject(list: ArrayList<Contact>): ArrayList<ContactObjetc> {
        var contact: ContactObjetc? = null
        listAux.clear()//limpo a lista para não ficar acumulando
        list.forEachIndexed { index, value ->

            if (index > 0 && (list[index - 1].name.first() !== value.name.first())) {
                contact = ContactObjetc()
                contact!!.letter = list[index - 1].name.first().toString()
                contact!!.contactList =
                    list.filter { it.name.startsWith(list[index - 1].name.first()) }
                listAux.add(contact!!)
            }
            if (index == list.size - 1) {
                contact = ContactObjetc()
                contact!!.letter = value.name.first().toString()
                contact!!.contactList = list.filter { it.name.startsWith(value.name.first()) }
                listAux.add(contact!!)
            }

        }
        return listAux
    }


}


