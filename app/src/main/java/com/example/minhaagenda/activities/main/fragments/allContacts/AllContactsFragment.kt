package com.example.minhaagenda.activities.main.fragments.allContacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener

import com.example.minhaagenda.shared.AppBarViewModel
import com.example.minhaagenda.adapters.ContactAdapter
import com.example.minhaagenda.animations.fade.FadeToViews.fadeInImmediately
import com.example.minhaagenda.animations.fade.FadeToViews.fadeOut
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactListByInitial
import com.example.minhaagendakotlin.databinding.FragmentAllContactsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AllContactsFragment : Fragment() {

    //objeto estático para gerenciar contatos selecionados no recyclerView(está sendo utilizado em várias partes da aplicação )
    companion object {
        // Conjunto mutável para armazenar os contatos selecionados
        private var selectedContacts = mutableSetOf<Contact>()

        // Retorna a lista de contatos selecionados
        fun getListSelectedContacts(): MutableSet<Contact> {
            return selectedContacts
        }

        // Verifica se um contato está na lista de selecionados
        fun alreadySelected(contact: Contact): Boolean {
            return selectedContacts.contains(contact)
        }

        // Retorna o número total de contatos selecionados
        fun getSizeSelectedContacts(): Int {
            return selectedContacts.size
        }

        // Adiciona um contato à lista de selecionados
        fun addSelectedContact(contact: Contact) {
            selectedContacts.add(contact)
        }

        // Remove um contato da lista de selecionados
        fun removeSelectedContact(contact: Contact) {
            selectedContacts.remove(contact)
        }

        // Limpa todos os contatos da lista de selecionados
        fun clearListSelectedContact() {
            selectedContacts.clear()
        }
    }


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
        //Configura o clique no botão de voltar do dispositivo
        backPressed()

        return binding.root

    }


    //Configuração do viewModel deste fragment
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
        val adapter = ContactAdapter(list, requireActivity())

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
                    fadeOut(binding.textLetter,200)
                }

            }

        })
        
    }

    override fun onResume() {
        super.onResume()
        viewModelAllContacts.getAllContacts()
    }

    // Método para lidar com o evento de pressionar o botão "Voltar"
    fun backPressed() {
        // Adiciona um callback ao onBackPressedDispatcher para personalizar o comportamento do botão "Voltar"
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Verifica se há contatos selecionados
                if (getSizeSelectedContacts() > 0) {
                    // Se houver contatos selecionados, limpa a lista de contatos selecionados
                    clearListSelectedContact()
                    // Recarrega o RecyclerView para atualizar a interface do usuário
                    reload_recyclerView("Desfazendo seleção")
                    // Atualiza o menu de opções para refletir as mudanças(não há contatos selecionado, então menu deve sumir)
                    requireActivity().invalidateOptionsMenu()
                } else {
                    // Se não houver contatos selecionados, finaliza a atividade
                    requireActivity().finish()
                }
            }
        })
    }

    // Método para recarregar o RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    fun reload_recyclerView(message:String) {
        // Lança uma coroutine na Main Thread para atualizar a UI
        lifecycleScope.launch(Dispatchers.Main) {
            // Torna visível o layout de progresso enquanto a lista está sendo recarregada
            binding.progressReloadList.apply {
                progressLayout.visibility = View.VISIBLE
                textProgressBar.text = message
            }
            // Aguarda 300 milissegundos para simular o carregamento
            delay(3000)
            // Notifica o adaptador do RecyclerView para atualizar a lista exibida
            binding.recyclerContact.adapter?.notifyDataSetChanged()
            // Oculta o layout de progresso após a atualização
            binding.progressReloadList.progressLayout.visibility = View.GONE
        }
    }

    // Método para recarregar a lista de contatos
    fun reloadContactList() {
        // Solicita ao ViewModel para obter todos os contatos
        viewModelAllContacts.getAllContacts()
        // Recarrega o RecyclerView para atualizar a interface do usuário com os novos dados
        reload_recyclerView("Recarregando Contatos")
    }


}


