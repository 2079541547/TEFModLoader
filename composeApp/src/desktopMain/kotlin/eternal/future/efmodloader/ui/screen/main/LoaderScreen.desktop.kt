package eternal.future.efmodloader.ui.screen.main

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
import eternal.future.efmodloader.State
import eternal.future.efmodloader.utility.FileUtils
import java.io.File
import java.util.UUID

@Composable
actual fun LoaderScreen.LoaderScreen() {
    var showInstall by remember { mutableStateOf(false) }
    var path by remember { mutableStateOf("") }

    if (showInstall) {
        LaunchedEffect(key1 = showInstall) {
            path = FileUtils.openFilePicker() ?: ""
            if (path.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    eternal.future.efmodloader.utility.EFModLoader.install(
                        path,
                        File(State.EFModLoaderPath, "${UUID.randomUUID()}").path
                    )
                    withContext(Dispatchers.Main) {
                        showInstall = false
                    }
                    loaders.value = eternal.future.efmodloader.utility.EFModLoader.loadLoadersFromDirectory(State.EFModLoaderPath)
                }
            } else {
                showInstall = false
            }
        }
    }

    LoaderScreen_r {
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