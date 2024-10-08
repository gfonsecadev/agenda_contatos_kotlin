package com.example.minhaagenda.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.minhaagenda.dividerRecyclerView.CustomDivider
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactListByInitial
import com.example.minhaagendakotlin.databinding.RecyclerContactLayoutBinding


//este adapter irá renderizar uma letra e a lista de contatos relacionado a esta letra para ser passada como parametro para o NestedAdapter aqui mesmo
//ou seja a letra popula este adapter e a lista o adapter aninhado.
class ContactAdapter(private var listContact: List<ContactListByInitial>, val context: Activity) : RecyclerView.Adapter<ContactHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        //retornamos para o holder um data binding do layout.
        val layout = RecyclerContactLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ContactHolder(layout,context)
    }

    override fun getItemCount(): Int {
        return listContact.size
    }

    //retorna o item de acordo com a posição passada
    //é utilizado no ScrollListener deste recyclerView para recuperar o item visivel no topo do recyclerView
    fun getItem(position: Int): ContactListByInitial? {
        if (listContact.isNotEmpty()){
            return listContact.get(position)}

        return null
    }

    //metodo para atualizar a lista do adapter somente se a lista passada for de tamanho diferente
    fun updateData(newListContact: List<ContactListByInitial>):Boolean{
        return isDifferentList(listContact,newListContact);
    }

    //metodo para distinguir se a atual lista e a que a substituirá são diferentes. Se diferentes atualizamos o adapter.
    fun isDifferentList(actualList:List<ContactListByInitial>, newListContact: List<ContactListByInitial>): Boolean {
        //esta condicional faz a comparação dos objetos por sobreescrito dos metodos equals e hashCode das classes ContactListByInitial e Contact
        //ou se de tamanho diferente é uma lista diferente
        if(actualList != newListContact || actualList.size != newListContact.size) {
            //como é diferente substituimos a atual pela nova
            listContact = newListContact
            return true
        }

        //se nenhuma condição satisfazer então a lista atual e a nova são as mesmas
        return false
    }


    override fun onBindViewHolder(holder: ContactHolder, @SuppressLint("RecyclerView") position: Int) {
        val contactObject = listContact.get(position)
        holder.firstLetter.text = contactObject.letter

        holder.settingsRecyclerView(contactObject.contactList)

    }

}

//viewHolder utilizando data binding
class ContactHolder(binding: RecyclerContactLayoutBinding, var context: Activity) : ViewHolder(binding.root) {
    val firstLetter = binding.firstLetter
    private val  recyclerNested = binding.recyclerNested


    //metodo para configurar recyclerView
    fun settingsRecyclerView(contacts:List<Contact>){
        // Cria uma nova instância de LinearLayoutManager para o RecyclerView
        val layout = LinearLayoutManager(context)

        // Define o número de itens a serem pré-carregados (prefetched) pelo LinearLayoutManager
        // para melhorar a performance de RecyclerViews aninhados
        layout.initialPrefetchItemCount = 1000
        //atribuimos o LinearLayout configurado ao recyclerView

        recyclerNested.layoutManager = layout

        // Indica que o tamanho do RecyclerView e seus itens são fixos, melhorando o desempenho
        recyclerNested.setHasFixedSize(true)

        recyclerNested.isNestedScrollingEnabled = false

        // Define o número de itens a serem mantidos no cache para melhorar a rolagem
        recyclerNested.setItemViewCacheSize(1000)

        //holder.recyclerNested.addItemDecoration(DividerItemDecoration(context,LinearLayout.VERTICAL)) //ou
        recyclerNested.addItemDecoration(CustomDivider())

        val adapter = NestedAdapter(contacts, context)
        recyclerNested.adapter = adapter

    }


}


