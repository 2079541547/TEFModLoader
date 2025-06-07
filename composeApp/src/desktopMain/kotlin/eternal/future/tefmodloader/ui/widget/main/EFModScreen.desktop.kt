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
import eternal.future.tefmodloader.data.EFMod
import eternal.future.tefmodloader.ui.screen.main.EFModScreen.mods
import eternal.future.tefmodloader.utility.FileUtils
import java.io.File
import java.util.UUID

@Composable
actual fun EFModScreen.EFModCard_o(mod: EFMod) {
    var showUpdate by remember { mutableStateOf(false) }
    var path by remember { mutableStateOf("") }

    if (showUpdate) {
        LaunchedEffect(key1 = showUpdate) {
            path = FileUtils.openFilePicker() ?: ""
            if (path.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    eternal.future.tefmodloader.utility.EFMod.install(
                        path,
                        File(State.EFModPath, "${UUID.randomUUID()}").path
                    )
                    withContext(Dispatchers.Main) {
                        showUpdate = false
                    }
                    mods.value = eternal.future.tefmodloader.utility.EFMod.loadModsFromDirectory(
                        State.EFModPath)
                }
            } else {
                showUpdate = false
            }
        }
    }



    EFModCard_Reuse(mod, onUpdateModClick = { showUpdate = true }) {
        showUpdate = true
    }

    if (showUpdate) {
        AlertDialog(
            onDismissRequest = { showUpdate = false },
            title = { Text(localesText.getString("update_mod")) },
            text = { Text("${localesText.getString("update_mod_content")} ${mod.info.name}?") },
            confirmButton = {},
            dismissButton = {}
        )
    }
}