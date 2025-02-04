package silkways.terraria.efmodloader.utility

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
actual fun selectFiles(CallBack: (List<Any>) -> Unit) {
    val selectModsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        CallBack(uris)
    }
    selectModsLauncher.launch("*/*")
}

@Composable
actual fun selectFile(CallBack: (Any) -> Unit) {
    val selectFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { url ->
        if (url != null) {
            CallBack(url)
        }
    }
    selectFileLauncher.launch("*/*")
}