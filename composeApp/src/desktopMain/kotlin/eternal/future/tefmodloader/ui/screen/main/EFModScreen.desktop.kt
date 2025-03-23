package eternal.future.tefmodloader.ui.screen.main

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.utility.FileUtils
import java.io.File
import java.util.UUID


@Composable
actual fun EFModScreen.EFModScreen() {
    var showInstall by remember { mutableStateOf(false) }
    var path by remember { mutableStateOf("") }

    if (showInstall) {
        LaunchedEffect(key1 = showInstall) {
            path = FileUtils.openFilePicker() ?: ""
            if (path.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    eternal.future.tefmodloader.utility.EFMod.install(
                        path,
                        File(State.EFModPath, "${UUID.randomUUID()}").path
                    )
                    withContext(Dispatchers.Main) {
                        showInstall = false
                    }
                    mods.value = eternal.future.tefmodloader.utility.EFMod.loadModsFromDirectory(State.EFModPath)
                }
            } else {
                showInstall = false
            }
        }
    }

    EFModScreen_r {
        showInstall = true
    }

    if (showInstall) {
        AlertDialog(
            onDismissRequest = { showInstall = false },
            title = { Text("Installing...") },
            text = { Text("") },
            confirmButton = {},
            dismissButton = {}
        )
    }
}