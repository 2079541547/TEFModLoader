package silkways.terraria.efmodloader.utility

import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser

object FileUtils {
    
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

    fun deleteDirectory(dir: File) {
        if (dir.isDirectory()) {
            val entries = dir.listFiles()
            if (entries != null) {
                for (entry in entries) {
                    deleteDirectory(entry)
                }
            }
        }
        if (!dir.delete()) {
            throw RuntimeException("Failed to delete file or directory: " + dir.path)
        }
    }

}


@Composable
expect fun selectFiles(CallBack: (List<Any>) -> Unit)

@Composable
expect fun selectFile(CallBack: (Any) -> Unit)