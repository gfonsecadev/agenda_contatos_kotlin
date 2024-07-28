package com.example.minhaagenda.shared

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minhaagenda.entities.SharedPreferencesHelper


//view model responsável pela logica de negocio
class LaunchersViewModel : ViewModel() {
    private val _uri = MutableLiveData<Uri>()
    val uri:LiveData<Uri> get() = _uri

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap:LiveData<Bitmap> = _bitmap

        lateinit var launcherGallery : ActivityResultLauncher<Intent>
        lateinit var preferencesHelper: SharedPreferencesHelper

        fun launcherGallery(context: ComponentActivity) {

            launcherGallery = context.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()){
                    result->
                if (result.resultCode == RESULT_OK && result.data != null){
                    _uri.value = result.data!!.data
                    preferencesHelper = SharedPreferencesHelper(context)
                    var bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver,_uri.value)
                    _uri.value?.let { preferencesHelper.editPreferencesImage(bitmap) }
                }
            }
        }

       fun openGallery(){
           val intent = Intent(Intent.ACTION_PICK).apply {
               type = "image/*"
           }
           launcherGallery.launch(intent)
       }






}