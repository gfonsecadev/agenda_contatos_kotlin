package com.example.minhaagenda.shared

import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher

//classe de ajuda para lidar com escolha de imagens(camera ou galeria)
class LaunchersImage {
    private lateinit var launcherRegister:ActivityResultLauncher<Intent>

        //recebe um register já configurado
        fun registerLauncher(launcher: ActivityResultLauncher<Intent>) {
            launcherRegister = launcher
        }

        //chama o launcher da galeria
       fun openGallery(){
           val intent = Intent(Intent.ACTION_PICK).apply {
               type = "image/*"
           }
           launcherRegister.launch(intent)
       }

    //chama o launcher da camera
     fun openCamera(){
         val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
         launcherRegister.launch(intent)
     }







}