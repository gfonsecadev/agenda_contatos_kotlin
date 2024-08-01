package com.example.minhaagenda.shared

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog

object Permissions {

    fun executeIfPermissionGranted(isGranted: Boolean, onPermissionGranted: () -> Unit,context: Context ) {
        if (isGranted) {
            onPermissionGranted()
        } else {
            requestPermissionsAgain(context)
        }
    }

    //metodo para permissôes quando negadas
     private fun requestPermissionsAgain(context: Context) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setTitle("Permissões Necessárias")
        alertBuilder.setMessage("Para continuar, precisamos da sua permissão para acessar a câmera e a galeria. Isso é necessário para que você possa escolher ou capturar uma imagem para o perfil. Por favor, vá para as Configurações e habilite as permissões necessárias.")
        alertBuilder.setPositiveButton("Ir para Configurações") { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.fromParts("package", context.packageName, null)
            }

            context.startActivity(intent)
        }
        alertBuilder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        val alert = alertBuilder.create()
        alert.show()


    }
}