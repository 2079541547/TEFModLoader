package eternal.future.tefmodloader.ui.widget.main

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
import eternal.future.tefmodloader.data.EFModLoader
import eternal.future.tefmodloader.ui.screen.main.LoaderScreen.loaders
import eternal.future.tefmodloader.utility.FileUtils
import java.io.File
import java.util.UUID

@Composable
actual fun LoaderScreen.LoaderCard_o(loader: EFModLoader) {
    var showUpdate by remember { mutableStateOf(false) }
    var path by remember { mutableStateOf("") }

    if (showUpdate) {
        LaunchedEffect(key1 = showUpdate) {
            path = FileUtils.openFilePicker() ?: ""
            if (path.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    eternal.future.tefmodloader.utility.EFMod.install(
                        path,
                        File(State.EFModLoaderPath, "${UUID.randomUUID()}").path
                    )
                    withContext(Dispatchers.Main) {
                        showUpdate = false
                    }
                    loaders.value = eternal.future.tefmodloader.utility.EFModLoader.loadLoadersFromDirectory(State.EFModLoaderPath)
                }
            } else {
                showUpdate = false
            }
        }
    }

    LoaderCard_Reuse(loader) {
        showUpdate = true
    }

    if (showUpdate) {
        AlertDialog(
            onDismissRequest = { showUpdate = false },
            title = { Text(localesText.getString("update_loader")) },
            text = { Text("${localesText.getString("update_loader_content")} ${loader.info.name}?") },
            confirmButton = {},
            dismissButton = {}
        )
    }
}