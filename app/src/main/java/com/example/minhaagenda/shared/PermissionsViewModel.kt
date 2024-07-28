package com.example.minhaagenda.shared

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionsViewModel : ViewModel() {

    private val _isPermissionGranted = MutableLiveData<Boolean>()
    val isPermissionGranted:LiveData<Boolean>  get() = _isPermissionGranted

    lateinit var permissionsLauncher:ActivityResultLauncher<Array<String>>

    fun permissions(context : ComponentActivity){
        permissionsLauncher = context.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            if (result.values.size > 0) {
                var denied = false
                for (permission in result.values) {
                    if (!permission) {
                        denied = true
                    }
                }
                //se denied = true significa que há permissôes negadas
                if (denied) {
                    //pedimos permissão novamente de forma manual
                    _isPermissionGranted.value = false
                } else {
                    //acesso liberado ao dialog de escolha da imagem
                    _isPermissionGranted.value = true
                }
            }
        }

    }

    fun askPermissions(permissions: Array<String>){
        permissionsLauncher.launch(permissions)
    }


}