package eternal.future.efmodloader.ui.widget.main

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
import eternal.future.efmodloader.data.EFModLoader
import eternal.future.efmodloader.ui.screen.main.EFModScreen.mods
import eternal.future.efmodloader.ui.screen.main.LoaderScreen.loaders
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun LoaderScreen.LoaderCard_o(loader: EFModLoader) {
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
            eternal.future.efmodloader.utility.EFModLoader.update(tempFile.path, loader.path)
            showUpdate = false
            mods.value = eternal.future.efmodloader.utility.EFMod.loadModsFromDirectory(State.EFModPath)
            loaders.value = eternal.future.efmodloader.utility.EFModLoader.loadLoadersFromDirectory(State.EFModLoaderPath)
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
            text = { Text("${EFModScreen.localesText.getString("update_loader_content")} ${loader.info.name}?") },
            confirmButton = {},
            dismissButton = {}
        )
    }
}