package eternal.future.tefmodloader.ui.widget.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import eternal.future.tefmodloader.MainActivity
import eternal.future.tefmodloader.MainApplication
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.data.EFMod
import eternal.future.tefmodloader.ui.screen.main.EFModScreen.mods
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun EFModScreen.EFModCard_o(mod: EFMod) {

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
            val r = eternal.future.tefmodloader.utility.EFMod.update(tempFile.path, mod.path)
            mods.value = eternal.future.tefmodloader.utility.EFMod.loadModsFromDirectory(State.EFModPath)
            if (!r.first) {
                errorMsg = if (r.second != "error") r.second else localesText.getString("update_mod_error_content")
                showError = true
            }
            showUpdate = false
        }
    }

    val modAPI = mapOf(
        "private-path" to File(mod.path, "private").absolutePath,
        "data-path" to mod.path
    )

    EFModCard_Reuse(
        mod,
        onUpdateModClick = {
            selectFileLauncher.launch("*/*")
            showUpdate = true
        },
        onModPageClick = {
            MainActivity.mainViewModel.navigateTo("modpage", mapOf(
                "title" to mod.info.name,
                "page-class" to mod.pageClass,
                "page-path" to File(mod.path,  "page/android.jar").absolutePath,
                "page-extraData" to modAPI
            ))
        }
    )

    if (showUpdate) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(localesText.getString("update_mod")) },
            text = {
                Column {
                    Text("${localesText.getString("update_mod_content")} ${mod.info.name}?")
                } },
            confirmButton = {},
            dismissButton = {}
        )
    }

    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text(localesText.getString("update_mod_error")) },
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