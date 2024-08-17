package com.example.minhaagenda.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.addSelectedContact
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.alreadySelected
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.getSizeSelectedContacts
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.removeSelectedContact
import com.example.minhaagenda.activities.showContact.ShowContactActivity
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactListByInitial
import com.example.minhaagenda.shared.ImageFormatConverter
import com.example.minhaagenda.shared.randomColor
import com.example.minhaagendakotlin.R

//este adapter será renderizado dentro do ContactAdapter por um recyclerView
class NestedAdapter(val list: ContactListByInitial, val context:Activity) :
    RecyclerView.Adapter<HolderNestedAdaper>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderNestedAdaper {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.recycler_nested, parent, false)
        return HolderNestedAdaper(layout,context)
    }

    override fun getItemCount(): Int {
        return list.contactList.size
    }


    override fun onBindViewHolder(holder: HolderNestedAdaper, position: Int) {
        // Obtém o contato na posição atual
        val contact = list.contactList[position]

        // Define o nome do contato no TextView
        holder.nameContact.text = contact.name

        // Cria uma cor RGB aleatória para contatos sem imagem
        val color = randomColor()

        // Configura a imagem do contato
        contact.image?.let {
            // Se o contato tem uma imagem, converte o byte array para imagem e carrega com Glide
            val image = ImageFormatConverter.byteArrayToImage(it)
            Glide.with(context)
                .load(image)
                .placeholder(R.drawable.choose_image) // Imagem de placeholder enquanto carrega
                .into(holder.imageContact)
        } ?: run {
            // Se o contato não tem imagem, usa uma cor de fundo aleatória
            Glide.with(context)
                .load(ColorDrawable(color)) // Converte a cor para Drawable
                .into(holder.imageContact)
            // Define a letra inicial do nome do contato
            holder.letterImageContact.text = contact.name.first().toString()
        }

        // Configura o comportamento ao realizar um clique longo
        holder.layoutContact.setOnLongClickListener {
            // Invalida o menu para atualizar a interface
            context.invalidateOptionsMenu()
            // Seleciona o contato e atualiza a visualização
            selectContact(holder, contact)
            true // Indica que o clique longo foi processado
        }

        // Configura o comportamento ao clicar brevemente
        holder.layoutContact.setOnClickListener {
            if (getSizeSelectedContacts() > 0) {
                // Se o modo de seleção está ativado, seleciona o contato e mostra o número de contatos selecionados
                selectContact(holder, contact)
                Toast.makeText(context, "${getSizeSelectedContacts()}", Toast.LENGTH_SHORT).show()
            } else {
                // Se o modo de seleção não está ativado, abre a atividade de detalhes do contato
                val intent = Intent(context, ShowContactActivity::class.java).apply {
                    putExtra("contact_id", contact.id) // Passa o ID do contato para a nova atividade
                }
                context.startActivity(intent)
            }
        }
    }

    private fun selectContact(holder: HolderNestedAdaper, contact: Contact) {
        if (alreadySelected(contact)) {
            // Se o contato já está selecionado, remove da lista de selecionados
            removeSelectedContact(contact)
            // Remove a marcação visual de seleção
            holder.layoutContact.setBackgroundResource(0) // Define a cor padrão (sem destaque)
        } else {
            // Se o contato não está selecionado, adiciona à lista de selecionados
            addSelectedContact(contact)
            // Adiciona uma marcação visual de seleção
            holder.layoutContact.setBackgroundResource(R.drawable.bg_button) // Define a cor de seleção
        }
    }


}

class HolderNestedAdaper(itemView: View,context: Context?) : ViewHolder(itemView) {
    var imageContact: ImageView = itemView.findViewById(R.id.contact_image)//circulo ao lado do contato
    var letterImageContact: TextView = itemView.findViewById(R.id.letterImage)//letra do nome do contato dentro do circulo
    var nameContact: TextView = itemView.findViewById(R.id.contact_name)// nome do contato
    var layoutContact: LinearLayout = itemView.findViewById(R.id.recycler_nested_layout)


}
