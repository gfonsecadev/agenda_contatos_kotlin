package com.example.minhaagenda.shared

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//view model responsável pela logica de negocio
class LaunchersViewModel : ViewModel() {
    private val _uri = MutableLiveData<Uri?>()
    val uri: MutableLiveData<Uri?> get() = _uri


        lateinit var launcherGallery : ActivityResultLauncher<Intent>

        fun registerLauncher(launcher: ActivityResultLauncher<Intent>) {
            launcherGallery = launcher
        }

       fun changeLiveData(uri: Uri){
           _uri.value = uri
       }

       fun openGallery(){
           val intent = Intent(Intent.ACTION_PICK).apply {
               type = "image/*"
           }
           launcherGallery.launch(intent)
       }

     fun openCamera(){
         val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
         launcherGallery.launch(intent)
     }






}