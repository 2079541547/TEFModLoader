package eternal.future.tefmodloader.ui.widget.main

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
import eternal.future.tefmodloader.data.EFModLoader
import eternal.future.tefmodloader.ui.screen.main.LoaderScreen.loaders
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun LoaderScreen.LoaderCard_o(loader: EFModLoader) {

    var showError by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    var showUpdate by remember { mutableStateOf(false) }
    val tempFile = File(MainApplication.getContext().externalCacheDir, "update.temp")
    val selectFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { url ->
        url?.let {
            MainApplication.getContext().contentResolver.openInputStream(url)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                    showUpdate = true
                }
            }
        }
    }

    if (showUpdate) {
        LaunchedEffect(key1 = showUpdate) {
            val r = eternal.future.tefmodloader.utility.EFModLoader.update(tempFile.path, loader.path)
            if (!r.first) {
                errorMsg = if (r.second != "error") r.second else EFModScreen.localesText.getString("update_loader_error_content")
                showError = true
            }
            loaders.value = eternal.future.tefmodloader.utility.EFModLoader.loadLoadersFromDirectory(State.EFModLoaderPath)
            showUpdate = false
        }
    }

    LoaderCard_Reuse(loader) {
        selectFileLauncher.launch("*/*")
        showUpdate = true
    }

    if (showUpdate) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(EFModScreen.localesText.getString("update_loader")) },
            text = {
                Column {
                    Text("${EFModScreen.localesText.getString("update_loader_content")} ${loader.info.name}?")
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
            title = { Text(EFModScreen.localesText.getString("update_loader_error")) },
            text = {
                LazyColumn {
                    item {
                        Text(errorMsg)
                    }
                } },
            confirmButton = {},
            dismissButton = {}
        )
    }
}