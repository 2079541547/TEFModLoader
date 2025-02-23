package eternal.future.efmodloader.ui.screen.welcome

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eternal.future.efmodloader.ui.widget.main.SettingScreen
import eternal.future.efmodloader.utility.FileUtils

@Composable
actual fun GuideScreen.disposition() {
    var selectedPath by remember { mutableStateOf("") }
    val pickFolder: () -> Unit = {
        val newPath = FileUtils.openFolderPicker()
        if (newPath != null) {
            selectedPath = newPath
            showNext_disposition.value = true
        }
    }

    Text(modifier = Modifier.fillMaxWidth().padding(10.dp), text = "Please enter the game path, otherwise you will not be able to proceed to the next step")

    SettingScreen.GeneralTextInput(
        title = "Choose Game Folder",
        value = selectedPath,
        onValueChange = { selectedPath = it },
        trailingIcon = {
            IconButton(onClick = {
                pickFolder()
            }) {
                Icon(Icons.Default.Folder, contentDescription = "编辑包名")
            }
        },
        modifier = Modifier.fillMaxWidth().padding(10.dp)
    )
}

@Composable
actual fun GuideScreen.disposition_2() {
    TODO("Not yet implemented")
}