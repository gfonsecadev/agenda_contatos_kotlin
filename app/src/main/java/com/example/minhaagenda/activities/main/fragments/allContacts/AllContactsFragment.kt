package com.example.minhaagenda.activities.main.fragments.allContacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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

    // Objeto estático para gerenciar contatos selecionados no RecyclerView (está sendo utilizado em várias partes da aplicação)
    companion object {
        // Conjunto mutável para armazenar os contatos selecionados
        private var selectedContacts = mutableSetOf<Contact>()
        //variavel para controlar a exibição dos menus
        private var isAllContactFragment = true


        //muda o estado da variavel
        fun changeAllContactFragment(value:Boolean){
            isAllContactFragment = value
        }

        //retorna a variavel
        fun isAllContactFragment(): Boolean {
            return isAllContactFragment
        }

        // Retorna a lista de contatos selecionados
        fun getListSelectedContacts(): List<Contact> {
            return selectedContacts.toList()
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

    // Instância do AppBarViewModel
    private val viewModelShare: AppBarViewModel by activityViewModels()
    private val viewModelAllContacts: AllContactsViewModel by viewModels { AllContactsViewModelFactory(requireActivity().application) }
    private lateinit var binding: FragmentAllContactsBinding
    private var listContactListByInitial: List<ContactListByInitial> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllContactsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnPreDraw {
            lifecycleScope.launch {
                // Aguarda o tempo necessário (550ms) antes de executar o código
                delay(500)
                // Configura o ViewModel para gerenciar o estado do AppBarLayout e searchView
                observeAppBarVisibility()
                // Configura o RecyclerView
                recyclerSettings(listContactListByInitial)
                // Configura o observador do ViewModel
                setupViewModelAllContacts()
                // Configura o clique no botão de voltar do dispositivo
                backPressed()

            }
        }
    }


    // Configuração do ViewModel deste fragment
    private fun setupViewModelAllContacts() {
        viewModelAllContacts.listContactListByInitial.observe(viewLifecycleOwner) {
            reloadAdapter(it)
        }
    }

    //metodo para atualizar a lista de contatos
    @SuppressLint("NotifyDataSetChanged")
    private fun reloadAdapter(newList:List<ContactListByInitial>){
        val adapter = binding.recyclerContact.adapter as ContactAdapter

        //se updateData retornar true então a nova lista passada é diferente, onde após isso o adapter é notificado da mudança exibindo um progress
        if (adapter.updateData(newList)){
            // Lança uma coroutine na Main Thread para atualizar a UI
            lifecycleScope.launch(Dispatchers.Main) {
                // Torna visível o layout de progresso enquanto a lista está sendo recarregada
                binding.progressReloadList.apply {
                    progressLayout.visibility = View.VISIBLE
                    textProgressBar.text = "Recarregando Contatos"
                }

                // Aguarda 300 milissegundos para simular o carregamento
                delay(300)
               //atualizamos o adapter pois a nova lista é diferente(houve mudanças)
                adapter.notifyDataSetChanged()
                // Oculta o layout de progresso após a atualização
                binding.progressReloadList.progressLayout.visibility = View.GONE
            }
        }

    }


    //Configura o comportamento da AppBar
    private fun observeAppBarVisibility(){
        //chamamos o metodo setAppBarLayoutState  para alterar o valor do MutableLiveData  e disparar o observer na actity passando os valores
        viewModelShare.setAppBarLayoutState(true)//appBar será exibida neste fragment
    }

    // Configuração do RecyclerView Principal
    private fun recyclerSettings(list: List<ContactListByInitial>) {
        // Cria uma nova instância de LinearLayoutManager para o RecyclerView
        val layout = LinearLayoutManager(context)

        // Atribui o LinearLayoutManager configurado ao RecyclerView
        binding.recyclerContact.layoutManager = layout

        // Indica que o tamanho do RecyclerView e seus itens são fixos, melhorando o desempenho
        binding.recyclerContact.setHasFixedSize(true)

        // Define o número de itens a serem mantidos no cache para melhorar a rolagem
        binding.recyclerContact.setItemViewCacheSize(1000)

        // O contactAdapter recebe uma lista de Contact onde o mapper contactToContactObject converte para ContactObject que é o exigido pelo adapter
        val adapter = ContactAdapter(list, requireActivity())
        binding.recyclerContact.adapter = adapter
        // Utilizo o ScrollListener para ouvir mudanças no scroll do RecyclerView
        binding.recyclerContact.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Recupero o LayoutManager
                val layout = recyclerView.layoutManager as LinearLayoutManager
                // Recupero o primeiro item visível do RecyclerView
                // O método getItem foi implementado por mim no atual adapter para retornar o item da lista de acordo com a posição passada
                val item = adapter.getItem(layout.findFirstVisibleItemPosition())
                val firstItemVisible = layout.findFirstVisibleItemPosition()
                // Passo a letra do ContactObject deste item
                binding.textLetter.text = item?.letter


            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //se estiver em movimento é exibido imediatamente
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    fadeInImmediately(binding.textLetter)
                } else {
                    //se parado vai desaparecendo
                    fadeOut(binding.textLetter, 500)
                }
            }
        })
    }


    // Limpa a lista de contatos selecionados e atualiza o menu (quando não há contatos selecionados, o menu é ocultado)
    private fun clearSelectedContactsAndRefreshMenu() {
        clearListSelectedContact()
        requireActivity().invalidateOptionsMenu()
    }

    // Método para lidar com o evento de pressionar o botão "Voltar"
    private fun backPressed() {
        // Adiciona um callback ao onBackPressedDispatcher para personalizar o comportamento do botão "Voltar"
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Verifica se há contatos selecionados
                if (getSizeSelectedContacts() > 0) {
                    // Se houver contatos selecionados, limpa a lista de contatos selecionados
                    clearListSelectedContact()
                    // Recarrega o adapter do RecyclerView para atualizar a interface do usuário
                    binding.recyclerContact.adapter?.notifyDataSetChanged()
                    // Atualiza o menu de opções para refletir as mudanças (não há contatos selecionados, então o menu deve sumir)
                    requireActivity().invalidateOptionsMenu()
                } else {
                    // Se não houver contatos selecionados, finaliza a Activity
                    requireActivity().finish()
                }
            }
        })
    }


    //metódos publicos abaixo serão utilizados pela MainActivity para manipular este fragment

    // Método para recarregar a lista de contatos fora deste fragment
    // Método para carregar a lista de contatos no BD
    // Método também utilizado neste fragment
    fun getAllContactList() {
        viewModelAllContacts.getAllContacts()
    }

    //metodo que passará para este fragment qual o nome a procurar no SearchView da MainActivity
    fun searchContacts(name: String){
        viewModelAllContacts.getContactByName(name)
    }

    //metodo que pedirá a este fragment retornar todos os contatos  quando o SearchView da MainActivity estiver em branco
    fun getAllContacts(){
        viewModelAllContacts.getAllContacts()
    }


    // Ao retornar para o fragmento, recarrega a lista de contatos
    // e atribui verdadeiro para a variavel que controla a exibição dos menus.
    override fun onResume() {
        super.onResume()
        getAllContactList()
        changeAllContactFragment(true)
        clearSelectedContactsAndRefreshMenu()
    }


    //Ao sair do fragment limpa os itens selecionados, e atribui falso para a variavel que controla a exibição dos menus para não serem exibidos em outros fragments.
    override fun onStop() {
        super.onStop()
        changeAllContactFragment(false)
        clearSelectedContactsAndRefreshMenu()
    }
}
