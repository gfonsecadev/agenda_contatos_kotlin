package com.example.minhaagenda.activities.main.fragments.importContacts

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.minhaagenda.activities.main.MainActivity
import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.shared.LauncherSearchContacts
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.FragmentImportContactsBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImportContactsFragment : Fragment() {

    private lateinit var binding: FragmentImportContactsBinding
    private  var launcherSearchContacts=LauncherSearchContacts()
    private val viewModelShare: AppBarViewModel by activityViewModels()
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
        // Configura o ViewModel para gerenciar o estado do AppBarLayout e searchView
        observeAppBarVisibility()
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
                    "text/csv" -> importByCsv(it)
                    "text/vcard", "text/x-vcard" -> importByVcf(it)
                    else -> {
                        println("Tipo de arquivo não suportado ou tipo MIME não reconhecido: $type")
                    }
                }
            }


        }

        launcherSearchContacts.registerLauncher(register)
    }

    //função para importar de um arquivo csv
    private fun importByCsv(file: Uri){
        lifecycleScope.launch {
            binding.importProgress.apply {
                textProgressBar.text = "Importando de Csv..."
                progressLayout.visibility = View.VISIBLE
                delay(1000)
                importContactsViewModel.importFromCsv(file)
                progressLayout.visibility = View.GONE
                redirectAllContact()
            }
        }
    }
    //função para importar de um arquivo vcf
    private fun importByVcf(file: Uri){
        lifecycleScope.launch {
            binding.importProgress.apply {
                textProgressBar.text =  "Importando de Vcf..."
                progressLayout.visibility = View.VISIBLE
                delay(1000)
                importContactsViewModel.importFromVcf(file)
                progressLayout.visibility = View.GONE
                redirectAllContact()
            }
        }
    }

    //método para redirecionar ao fragment que mostra todos os contatos.
    private fun redirectAllContact(){
        (requireActivity() as MainActivity).changeFragmentNavController(R.id.nav_item_import)
    }


    //Configura o comportamento da AppBar
    private fun observeAppBarVisibility(){
        //chamamos o metodo setAppBarLayoutState  para alterar o valor do MutableLiveData  e disparar o observer na actity passando os valores
        viewModelShare.setAppBarLayoutState(false)//appBar não será exibida neste fragment
    }

}