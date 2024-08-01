package com.example.minhaagenda.shared

import android.Manifest
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionsViewModel() : ViewModel() {

    private val _isPermissionGranted = MutableLiveData<Boolean>()
    val isPermissionGranted:LiveData<Boolean>  get() = _isPermissionGranted

    private lateinit var permissionsLauncher:ActivityResultLauncher<Array<String>>

    fun registerLauncherPermissions(launcher: ActivityResultLauncher<Array<String>>){
        permissionsLauncher = launcher
    }


    // Solicita as permissões necessárias para acessar a galeria ou câmera
    // Este método adapta a solicitação de permissões com base na versão do sistema operacional,pois houveram mudanças de como isso deve ser feito
    fun askPermissions(){
        val permissions = if (Build.VERSION.SDK_INT < VERSION_CODES.TIRAMISU) {
            // Se a versão do SDK for anterior à Android 13 (TIRAMISU)
            arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE, // Permissão para ler arquivos de armazenamento externo
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, // Permissão para gravar arquivos de armazenamento externo
                    Manifest.permission.CAMERA // Permissão para usar a câmera do dispositivo
                )

        } else {
            // Se a versão do SDK for Android 13 (TIRAMISU) ou superior
            arrayOf(
                    Manifest.permission.CAMERA, // Permissão para usar a câmera do dispositivo
                    Manifest.permission.READ_MEDIA_IMAGES // Permissão para acessar imagens de mídia (substitui a permissão READ_EXTERNAL_STORAGE em Android 13)
                )

        }
        permissionsLauncher.launch(permissions)
    }

    fun changeLiveData(value : Boolean){
        _isPermissionGranted.value = value
    }




}