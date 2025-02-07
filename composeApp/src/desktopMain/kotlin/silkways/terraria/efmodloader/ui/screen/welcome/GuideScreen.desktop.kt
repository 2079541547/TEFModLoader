package silkways.terraria.efmodloader.ui.screen.welcome

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import silkways.terraria.efmodloader.ui.widget.main.SettingScreen
import silkways.terraria.efmodloader.utility.FileUtils

@Composable
actual fun GuideScreen.disposition() {
    var selectedPath by remember { mutableStateOf("") }
    val pickFolder: () -> Unit = {
        val newPath = FileUtils.openFilePicker()
        if (newPath != null) {
            selectedPath = newPath
            showNext_disposition.value = true
        }
    }

    Text(modifier = Modifier.fillMaxWidth().padding(10.dp), text = "Please enter the game path, otherwise you will not be able to proceed to the next step")

    SettingScreen.PathInputWithFilePicker(
        title = "Choose Game Folder",
        path = selectedPath,
        onPathChange = { newPath -> selectedPath = newPath },
        onFolderSelect = pickFolder,
        modifier = Modifier.fillMaxWidth().padding(10.dp)
    )
}

@Composable
actual fun GuideScreen.disposition_2() {
    TODO("Not yet implemented")
}