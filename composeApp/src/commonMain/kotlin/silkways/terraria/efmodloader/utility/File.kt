package silkways.terraria.efmodloader.utility

import androidx.compose.runtime.Composable
import javax.swing.JFileChooser

object File {
    fun openFilePicker(): String? {
        val fileChooser = JFileChooser()
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val returnValue = fileChooser.showOpenDialog(null)
        return if (returnValue == JFileChooser.APPROVE_OPTION) {
            fileChooser.selectedFile.absolutePath
        } else {
            null
        }
    }

}


@Composable
expect fun selectFiles(CallBack: (List<Any>) -> Unit)

@Composable
expect fun selectFile(CallBack: (Any) -> Unit)