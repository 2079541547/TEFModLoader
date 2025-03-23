package eternal.future.tefmodloader.ui.screen.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import eternal.future.tefmodloader.MainApplication
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.utility.EFMod
import eternal.future.tefmodloader.utility.Locales
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
actual fun EFModScreen.EFModScreen() {

    locale.loadLocalization("Screen/MainScreen/EFModScreen.toml", Locales.getLanguage(State.language.value))


    var showError by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

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
            val r = EFMod.install(tempFile.path, File(State.EFModPath, UUID.randomUUID().toString()).path)
            tempFile.delete()
            if (!r.first) {
                errorMsg = if (r.second != "error") r.second else locale.getString("install_error_content")
                showError = true
            }
            mods.value = EFMod.loadModsFromDirectory(State.EFModPath)
            showInstall = false
        }
    }

    EFModScreen_r {
        selectFileLauncher.launch("*/*")
    }

    if (showInstall) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(locale.getString("installing")) },
            text = {
                Column {
                    Text(locale.getString("installing_content"))
                    CircularProgressIndicator()
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text(locale.getString("install_error")) },
            text = {
                LazyColumn {
                    item {
                        Text(errorMsg)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}