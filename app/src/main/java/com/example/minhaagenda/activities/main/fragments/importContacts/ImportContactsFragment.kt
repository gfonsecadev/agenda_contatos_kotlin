package com.example.minhaagenda.activities.main.fragments.importContacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.shared.LauncherSearchContacts
import com.example.minhaagendakotlin.databinding.FragmentImportContactsBinding

class ImportContactsFragment : Fragment() {

    private lateinit var binding: FragmentImportContactsBinding
    private  var launcherSearchContacts=LauncherSearchContacts()
    private val importContactsViewModel: ImportContactsViewModel by viewModels{ImportContactsViewModelFactory(requireActivity().application)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImportContactsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configura o ViewModel para gerenciar o estado do AppBarLayout
        setupViewModelAppBar()
        registerLaunchers()
        buttomListeners()
    }

    private fun buttomListeners(){
        binding.vcfFormat.setOnClickListener{
            launcherSearchContacts.searchByVcfFile()
        }

        binding.csvFormat.setOnClickListener{
            launcherSearchContacts.searchByCsvFile()
        }
    }

    private fun registerLaunchers(){
        val register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            result.data?.data?.let {
                val type = requireActivity().contentResolver.getType(it)

                when (type?.lowercase()) {
                    "text/csv" -> importContactsViewModel.importFromCsv(it)
                    "text/vcard", "text/x-vcard" -> importContactsViewModel.importFromVcf(it)
                    else -> {
                        println("Tipo de arquivo não suportado ou tipo MIME não reconhecido: $type")
                    }
                }
            }


        }

        launcherSearchContacts.registerLauncher(register)
    }


    private fun setupViewModelAppBar() {
        //instãncia do AppBarViewModel
        val viewModelShare = ViewModelProvider(requireActivity()).get(AppBarViewModel::class.java)
        //chamamos o metodo setAppBarLayoutState para alterar o valor do MutableLiveData  e disparar o observer na actity passando o boleano
        viewModelShare.setAppBarLayoutState(false)//appBar não será exibida neste fragment
    }

}