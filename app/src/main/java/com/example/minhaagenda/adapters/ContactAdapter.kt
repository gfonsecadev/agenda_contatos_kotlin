package com.example.minhaagenda.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.minhaagenda.DividerRecyclerView.CustomDivider
import com.example.minhaagenda.entities.ContactListByInitial
import com.example.minhaagendakotlin.databinding.RecyclerContactLayoutBinding

//este adapter irá renderizar uma letra e a lista de contatos relacionado a esta letra para ser passada como parametro para o NestedAdapter aqui mesmo
//ou seja a letra popula este adapter e a lista o adapter aninhado.
class ContactAdapter(var listContact: List<ContactListByInitial>, val context: Activity) : RecyclerView.Adapter<ContactHolder>() {

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

    //metodo para atualizar a lista do adapter
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newListContact: List<ContactListByInitial>){
        listContact = newListContact
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ContactHolder, @SuppressLint("RecyclerView") position: Int) {
        val contactObject = listContact.get(position)
        holder.firstLetter.text = contactObject.letter.toString()

        //instãnciamos o recycler view.
        holder.recyclerNested.layoutManager = LinearLayoutManager(context)

        // Indica que o tamanho do RecyclerView e seus itens são fixos, melhorando o desempenho
        holder.recyclerNested.setHasFixedSize(true)

        // Define o número de itens a serem mantidos no cache para melhorar a rolagem
        holder.recyclerNested.setItemViewCacheSize(20)

        //holder.recyclerNested.addItemDecoration(DividerItemDecoration(context,LinearLayout.VERTICAL)) //ou
        holder.recyclerNested.addItemDecoration(CustomDivider())

        val adapter = NestedAdapter(contactObject, context)
        holder.recyclerNested.adapter = adapter

    }

}

//viewHolder utilizando data binding
class ContactHolder(private val binding: RecyclerContactLayoutBinding,context: Context?) : ViewHolder(binding.root) {
    val firstLetter = binding.firstLetter
    val recyclerNested = binding.recyclerNested

}
