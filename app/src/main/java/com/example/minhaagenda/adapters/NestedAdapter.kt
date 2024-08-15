package com.example.minhaagenda.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.minhaagenda.activities.showContact.ShowContactActivity
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.shared.ImageFormatConverter
import com.example.minhaagenda.shared.randomColor
import com.example.minhaagendakotlin.R

//este adapter será renderizado dentro do ContactAdapter por um recyclerView
class NestedAdapter(val list: List<Contact>,val context:Context) :
    RecyclerView.Adapter<HolderNestedAdaper>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderNestedAdaper {
        var layout = LayoutInflater.from(parent.context).inflate(R.layout.recycler_nested, parent, false)
        return HolderNestedAdaper(layout,context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): Contact {
        return list.get(position)
    }

    override fun onBindViewHolder(holder: HolderNestedAdaper, position: Int) {
        var contact = list.get(position)
        holder.nameContact.text = contact.name


        // criar uma cor RGB através da extensâo randomColor criada.
        val color = randomColor()

        // Aplicando a view
            contact.image?.let{
                val image =  ImageFormatConverter.byteArrayToImage(it)
                Glide.with(context).load(image).placeholder(R.drawable.choose_image).into(holder.imageContact)
            } ?: run {
                Glide.with(context).load(ColorDrawable(color)).into(holder.imageContact)
                holder.letterImageContact.text = contact.name.first().toString()}


            holder.layoutContact.setOnClickListener{
                val intent = Intent(context,ShowContactActivity::class.java)
                intent.putExtra("contact_id",contact.id)
                context.startActivity(intent)

            }

    }


}

class HolderNestedAdaper(itemView: View,context: Context?) : ViewHolder(itemView) {
    var imageContact = itemView.findViewById<ImageView>(R.id.contact_image)//circulo ao lado do contato
    var letterImageContact = itemView.findViewById<TextView>(R.id.letterImage)//letra do nome do contato dentro do circulo
    var nameContact = itemView.findViewById<TextView>(R.id.contact_name)// nome do contato
    var layoutContact = itemView.findViewById<LinearLayout>(R.id.recycler_nested_layout)

}
