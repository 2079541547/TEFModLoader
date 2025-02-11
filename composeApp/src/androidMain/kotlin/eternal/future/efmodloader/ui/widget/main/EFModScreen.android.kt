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
import eternal.future.efmodloader.data.EFMod
import eternal.future.efmodloader.ui.screen.main.EFModScreen.mods
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun EFModScreen.EFModCard_o(mod: EFMod) {

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
            eternal.future.efmodloader.utility.EFMod.update(tempFile.path, mod.path)
            mods.value = eternal.future.efmodloader.utility.EFMod.loadModsFromDirectory(State.EFModPath)
            showUpdate = false
        }
    }

    EFModCard_Reuse(mod) {
        selectFileLauncher.launch("*/*")
        showUpdate = true
    }

    if (showUpdate) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(localesText.getString("update_mod")) },
            text = { Text("${localesText.getString("update_mod_content")} ${mod.info.name}?") },
            confirmButton = {},
            dismissButton = {}
        )
    }
}