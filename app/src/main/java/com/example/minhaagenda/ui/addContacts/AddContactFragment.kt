package com.example.minhaagenda.ui.addContacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.minhaagendakotlin.databinding.FragmentAddContactBinding


class AddContactFragment : Fragment() {
 lateinit var binding:FragmentAddContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddContactBinding.inflate(inflater)

        return binding.root

    }

}