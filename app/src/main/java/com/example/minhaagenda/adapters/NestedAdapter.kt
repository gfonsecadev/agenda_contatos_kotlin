package com.example.minhaagenda.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.minhaagenda.activities.showContact.ShowContactActivity
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.shared.ImageFormatConverter
import com.example.minhaagendakotlin.R

//este adapter será renderizado dentro do ContactAdapter por um recyclerView
class NestedAdapter(val list: List<Contact>,val context:Context?) :
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
        holder.name.text = contact.name


        //gerando cores r,g,b aleatórias.
        val red = (0..255).random()
        val green = (0..255).random()
        val blue = (0..255).random()

        // Combinando as componentes para criar uma cor RGB
        val color = Color.rgb(red, green, blue)

        // Aplicando a view
        contact.image?.let{
            holder.view.background =  BitmapDrawable(context?.resources,ImageFormatConverter.byteArrayToImage(it))
        } ?: run { holder.view.setBackgroundColor(color)
               holder.letterImage.text = contact.name.first().toString()}

            holder.layoutContact.setOnClickListener{
                val intent = Intent(context,ShowContactActivity::class.java)
                intent.putExtra("contact_id",contact.id)
                context?.startActivity(intent)

            }


    }


}

class HolderNestedAdaper(itemView: View,context: Context?) : ViewHolder(itemView) {
    var view = itemView.findViewById<View>(R.id.contact_image)//circulo ao lado do contato
    var letterImage = itemView.findViewById<TextView>(R.id.letterImage)//letra do nome do contato dentro do circulo
    var name = itemView.findViewById<TextView>(R.id.contact_name)// nome do contato
    var layoutContact = itemView.findViewById<LinearLayout>(R.id.recycler_nested_layout)

}
