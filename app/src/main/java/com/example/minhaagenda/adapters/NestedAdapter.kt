package com.example.minhaagenda.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
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
import com.example.minhaagenda.activities.main.MainActivity.Companion.typedNameToSearch
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.addSelectedContact
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.alreadySelected
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.getSizeSelectedContacts
import com.example.minhaagenda.activities.main.fragments.allContacts.AllContactsFragment.Companion.removeSelectedContact
import com.example.minhaagenda.activities.showContact.ShowContactActivity
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.entities.ContactListByInitial
import com.example.minhaagenda.shared.randomColor
import com.example.minhaagendakotlin.R

//este adapter será renderizado dentro do ContactAdapter por um recyclerView
class NestedAdapter(val list: ContactListByInitial, val activity:Activity) :
    RecyclerView.Adapter<HolderNestedAdaper>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderNestedAdaper {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.recycler_nested, parent, false)
        return HolderNestedAdaper(layout)
    }

    override fun getItemCount(): Int {
        return list.contactList.size
    }


    override fun onBindViewHolder(holder: HolderNestedAdaper, position: Int) {
        // Obtém o contato na posição atual
        val contact = list.contactList[position]

        // Define o nome do contato no TextView
        holder.nameContact.text = contact.name
        highlightText(holder.nameContact, typedNameToSearch)

        // Cria uma cor RGB  com base no nome para contatos sem imagem
        val color = randomColor(contact.name)

        // Configura a imagem do contato
        contact.image?.let {
            // Se o contato tem uma imagem, converte o byte array com Glide para setar a imagem
            Glide.with(activity.application)
                .load(it)
                .placeholder(R.drawable.choose_image) // Imagem de placeholder enquanto carrega
                .into(holder.imageContact)
        } ?: run {
            // Se o contato não tem imagem, usa uma cor de fundo aleatória
            Glide.with(activity.application)
                .load(ColorDrawable(color)) // Converte a cor para Drawable
                .into(holder.imageContact)
            // Define a letra inicial do nome do contato
            holder.letterImageContact.text = contact.name.first().toString()
        }

        // Configura o comportamento ao realizar um clique longo
        holder.layoutContact.setOnLongClickListener {
            // Seleciona o contato e atualiza a visualização
            selectContact(holder, contact)
            // Invalida o menu para atualizar a interface e exibir o menu
            activity.invalidateOptionsMenu()
            true // Indica que o clique longo foi processado
        }

        // Configura o comportamento ao clicar brevemente
        holder.layoutContact.setOnClickListener {
            if (getSizeSelectedContacts() > 0) {
                // Se o modo de seleção está ativado, seleciona o contato e mostra o número de contatos selecionados
                selectContact(holder, contact)
            } else {
                // Se o modo de seleção não está ativado, abre a atividade de detalhes do contato
                val intent = Intent(activity, ShowContactActivity::class.java).apply {
                    putExtra("contact_id", contact.id) // Passa o ID do contato para a nova atividade
                }
                activity.startActivity(intent)
            }
        }
    }

    fun highlightText(textView: TextView, textToHighlight: String) {
        // Obtém o texto atual do TextView
        val originalText = textView.text.toString()

        // Cria um SpannableString a partir do texto original para permitir a aplicação de estilos
        val spannableString = SpannableString(originalText)

        // Encontra a posição inicial do texto que deve ser destacado
        val startIndex = originalText.indexOf(textToHighlight, ignoreCase = true)

        // Verifica se o texto para destacar foi encontrado
        if (startIndex != -1) {
            // Cria um span para definir a cor do texto destacado
            val colorSpan = ForegroundColorSpan(activity.getColor(R.color.main_orange))

            // Aplica o span de cor ao texto encontrado
            spannableString.setSpan(
                colorSpan,
                startIndex,
                startIndex + textToHighlight.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Cria um span para definir o estilo de negrito
            val styleSpan = StyleSpan(Typeface.BOLD)

            // Aplica o span de estilo ao texto encontrado
            spannableString.setSpan(
                styleSpan,
                startIndex,
                startIndex + textToHighlight.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Atualiza o TextView com o texto formatado que inclui os estilos aplicados
        textView.text = spannableString
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
            holder.layoutContact.setBackgroundResource(R.drawable.background_selected_contacts) // Define a cor de seleção
        }
        //se lista de selecionados estiver vazia o menu irá sumir
        if(getSizeSelectedContacts()==0){
            activity.invalidateOptionsMenu()
        }
    }


}

class HolderNestedAdaper(itemView: View) : ViewHolder(itemView) {
    var imageContact: ImageView = itemView.findViewById(R.id.contact_image)//circulo ao lado do contato
    var letterImageContact: TextView = itemView.findViewById(R.id.letterImage)//letra do nome do contato dentro do circulo
    var nameContact: TextView = itemView.findViewById(R.id.contact_name)// nome do contato
    var layoutContact: LinearLayout = itemView.findViewById(R.id.recycler_nested_layout)


}
