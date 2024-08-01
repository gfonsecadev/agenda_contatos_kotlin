package com.example.minhaagenda.shared

import android.Manifest
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.activity.result.ActivityResultLauncher

//classe de ajuda para lidar com permissôes(leitura, escrita e camera)
class LauncherPermissions{
    private lateinit var permissionsLauncher:ActivityResultLauncher<Array<String>>

    //recebe um register já configurado
    fun registerLauncherPermissions(launcher: ActivityResultLauncher<Array<String>>){
        permissionsLauncher = launcher
    }

    //chama o launcher de permissôes com o array de permissôes já definido
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


}