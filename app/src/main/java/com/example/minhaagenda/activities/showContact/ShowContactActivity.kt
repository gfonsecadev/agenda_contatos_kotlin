package com.example.minhaagenda.activities.showContact

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.minhaagenda.activities.main.fragments.addContacts.AddContactFragment
import com.example.minhaagenda.entities.Contact
import com.example.minhaagenda.shared.ImageFormatConverter
import com.example.minhaagendakotlin.R
import com.example.minhaagendakotlin.databinding.ActivityShowContactBinding

class ShowContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowContactBinding
    private val viewModelShowContact:ShowContactActivityViewModel by viewModels{ ShowContactActivityViewModelFactory(application)}
    private lateinit var  contactReceived:Contact
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        receiveIdContact()
        observeViewModel()
        clicables()
    }



   private fun clicables(){
       binding.clickShare.setOnClickListener{
           Toast.makeText(this,"Compartilhar",Toast.LENGTH_SHORT).show()
       }
       binding.clickEdit.setOnClickListener{
           callFragment()
       }
       binding.clickDelete.setOnClickListener{
           viewModelShowContact.getDeleteContact()
           finish()
       }
   }
    private fun callFragment(){
        val fragment = AddContactFragment().hasDataToUpdate(contactReceived)
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.container, AddContactFragment())
        fragmentTransition.setCustomAnimations(R.anim.enter_fragment,R.anim.close_fragment)
        fragmentTransition.commit()
    }

    fun receiveIdContact(){
        val contactId = intent.getIntExtra("contact_id",-1)
        if (contactId > 0){
            viewModelShowContact.getContact(contactId)
        }
    }

    fun observeViewModel(){
        viewModelShowContact.contact.observe(this){
            contact->
            contactReceived = contact

            contact.image?.let {
                binding.showContactImage.setImageBitmap(ImageFormatConverter.byteArrayToImage(it))
            }

            binding.showTextName.text = contact.name
            binding.showTextPhone.text = contact.phone
            if(contact.email.isNullOrBlank()) binding.showTextEmail.visibility = View.GONE else binding.showTextEmail.text = contact.email

        }
    }



}