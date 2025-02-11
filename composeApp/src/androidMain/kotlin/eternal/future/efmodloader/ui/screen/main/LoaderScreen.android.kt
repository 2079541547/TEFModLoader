package eternal.future.efmodloader.ui.screen.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import eternal.future.efmodloader.MainApplication
import eternal.future.efmodloader.State
import eternal.future.efmodloader.utility.EFModLoader
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
actual fun LoaderScreen.LoaderScreen() {
    var showInstall by remember { mutableStateOf(false) }

    val tempFile = File(MainApplication.getContext().externalCacheDir, "install.temp")

    val selectFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { url ->
        url?.let {
            MainApplication.getContext().contentResolver.openInputStream(url)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                    showInstall = true
                }
            }
        }
    }

    if (showInstall) {
        LaunchedEffect(key1 = showInstall) {
            EFModLoader.install(tempFile.path, File(State.EFModLoaderPath, UUID.randomUUID().toString()).path)
            tempFile.delete()
            loaders.value = EFModLoader.loadLoadersFromDirectory(State.EFModLoaderPath)
            showInstall = false
        }
    }

    LoaderScreen_r {
        selectFileLauncher.launch("*/*")
    }

    if (showInstall) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text("Installing...") },
            text = { Text("") },
            confirmButton = {},
            dismissButton = {}
        )
    }
}