package eternal.future.tefmodloader.utility

import androidx.compose.runtime.Composable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.swing.JFileChooser

object FileUtils {

    fun openFolderPicker(): String? {
        EFLog.d("打开文件夹选择器")
        val fileChooser = JFileChooser()
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val returnValue = fileChooser.showOpenDialog(null)

        return if (returnValue == JFileChooser.APPROVE_OPTION) {
            val selectedPath = fileChooser.selectedFile.absolutePath
            EFLog.i("用户选择了文件夹: $selectedPath")
            selectedPath
        } else {
            EFLog.w("用户取消了文件夹选择")
            null
        }
    }

    fun openFilePicker(): String? {
        EFLog.d("打开文件选择器")
        val fileChooser = JFileChooser()
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        val returnValue = fileChooser.showOpenDialog(null)

        return if (returnValue == JFileChooser.APPROVE_OPTION) {
            val selectedPath = fileChooser.selectedFile.absolutePath
            EFLog.i("用户选择了文件: $selectedPath")
            selectedPath
        } else {
            EFLog.w("用户取消了文件选择")
            null
        }
    }

    fun deleteDirectory(dir: File) {
        EFLog.d("开始删除目录: ${dir.absolutePath}")
        if (dir.isDirectory) {
            val entries = dir.listFiles()
            if (entries != null) {
                for (entry in entries) {
                    EFLog.v("递归删除子项: ${entry.absolutePath}")
                    deleteDirectory(entry)
                }
            }
        }
        if (!dir.delete()) {
            EFLog.e("无法删除文件或目录: ${dir.path}")
            throw RuntimeException("Failed to delete file or directory: " + dir.path)
        } else {
            EFLog.v("成功删除文件或目录: ${dir.path}")
        }
    }

    fun copyRecursivelyEfficient(source: File, target: File) {
        EFLog.d("开始递归复制: ${source.absolutePath} 到 ${target.absolutePath}")
        if (source.isDirectory) {
            EFLog.v("源是目录: ${source.name}")
            if (!target.exists()) {
                target.mkdirs()
                EFLog.v("创建目标目录: ${target.absolutePath}")
            }

            val files = source.listFiles()
            if (files != null) {
                for (file in files) {
                    val targetFile = File(target, file.name)
                    copyRecursivelyEfficient(file, targetFile)
                }
            }
        } else {
            EFLog.v("源是文件: ${source.name}")
            try {
                FileInputStream(source).use { input ->
                    FileOutputStream(target).use { output ->
                        EFLog.v("开始从 ${source.absolutePath} 复制到 ${target.absolutePath}")
                        input.copyTo(output)
                        EFLog.v("完成从 ${source.absolutePath} 复制到 ${target.absolutePath}")
                    }
                }
            } catch (e: IOException) {
                EFLog.e("复制过程中发生IO异常", e)
                e.printStackTrace()
            }
        }
        EFLog.d("完成递归复制: ${source.absolutePath} 到 ${target.absolutePath}")
    }

    fun moveRecursivelyEfficient(source: File, target: File) {
        try {
            if (source.isDirectory) {
                EFLog.v("源是目录: ${source.name}")

                if (source.renameTo(target)) {
                    EFLog.v("成功通过重命名移动目录: ${source.absolutePath} 到 ${target.absolutePath}")
                    return
                }

                EFLog.v("直接重命名目录失败，改用递归方式")
                if (!target.exists()) {
                    target.mkdirs()
                    EFLog.v("创建目标目录: ${target.absolutePath}")
                }

                source.listFiles()?.forEach { file ->
                    val targetFile = File(target, file.name)
                    moveRecursivelyEfficient(file, targetFile)
                }

                if (source.delete()) {
                    EFLog.v("成功删除空目录: ${source.absolutePath}")
                } else {
                    EFLog.e("无法删除目录: ${source.absolutePath}")
                }

            } else {
                EFLog.v("源是文件: ${source.name}")
                if (source.renameTo(target)) {
                    EFLog.v("成功通过重命名移动文件: ${source.absolutePath} 到 ${target.absolutePath}")
                } else {
                    EFLog.v("重命名失败，尝试复制+删除方式")
                    source.copyTo(target, overwrite = true)
                    if (source.delete()) {
                        EFLog.v("成功删除源文件: ${source.absolutePath}")
                    } else {
                        EFLog.e("无法删除源文件: ${source.absolutePath}")
                    }
                }
            }
        } catch (e: IOException) {
            EFLog.e("移动过程中发生IO异常", e)
        }
    }
}


@Composable
expect fun selectFiles(CallBack: (List<Any>) -> Unit)

@Composable
expect fun selectFile(CallBack: (Any) -> Unit)