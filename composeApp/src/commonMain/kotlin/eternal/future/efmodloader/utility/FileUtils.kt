package eternal.future.efmodloader.utility

import androidx.compose.runtime.Composable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.swing.JFileChooser

object FileUtils {
    
    fun openFolderPicker(): String? {
        val fileChooser = JFileChooser()
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val returnValue = fileChooser.showOpenDialog(null)
        return if (returnValue == JFileChooser.APPROVE_OPTION) {
            fileChooser.selectedFile.absolutePath
        } else {
            null
        }
    }

    fun openFilePicker(): String? {
        val fileChooser = JFileChooser()
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
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

    fun copyRecursivelyEfficient(source: File, target: File) {
        if (source.isDirectory) {
            if (!target.exists()) {
                target.mkdirs()
            }

            val files = source.listFiles()
            if (files != null) {
                for (file in files) {
                    val targetFile = File(target, file.name)
                    copyRecursivelyEfficient(file, targetFile)
                }
            }
        } else {
            try {
                FileInputStream(source).use { input ->
                    FileOutputStream(target).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}


@Composable
expect fun selectFiles(CallBack: (List<Any>) -> Unit)

@Composable
expect fun selectFile(CallBack: (Any) -> Unit)