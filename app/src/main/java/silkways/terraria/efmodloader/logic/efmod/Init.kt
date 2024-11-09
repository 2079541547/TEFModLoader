package silkways.terraria.efmodloader.logic.efmod

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.data.TEFModLoader
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.utils.FileUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

/*******************************************************************************
 * 文件名称: Init
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/3 下午5:28
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

class Init(private val context: Context) {

    private var loadingDialog: AlertDialog? = null

    @SuppressLint("SdCardPath")
    fun initialization() {
        showLoadingDialog()
        // 模拟加载过程
        Thread {

            try {
                when (SPUtils.readInt(Settings.jsonPath, 0)) {
                    0 -> {
                        FileUtils.deleteDirectory(File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFModX/"))
                        FileUtils.deleteDirectory(File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFMod/"))
                        syncDirectories(File(context.getExternalFilesDir(null), "EFMod-Private/"), File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/export/private/"))
                        copyFilesFromToOgg(File(context.getExternalFilesDir(null), "EFMod-Private/"), File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/Private/"))
                    }

                    1 -> {
                        val a = JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.GamePackageName) as String
                        FileUtils.deleteDirectory(File("data/data/$a/cache/EFModX/"))
                        FileUtils.deleteDirectory(File("data/data/$a/cache/EFMod/"))
                        syncDirectories(File(context.getExternalFilesDir(null), "EFMod-Private/"), File("/sdcard/Android/data/${SPUtils.readString(Settings.GamePackageName, "com.and.games505.TerrariaPaid") as String}/files/EFMod-Private/"))
                        copyFilesFromTo(File(context.getExternalFilesDir(null), "EFMod-Private/"), File("/sdcard/Android/data/${SPUtils.readString(Settings.GamePackageName, "com.and.games505.TerrariaPaid") as String}/files/EFMod-Private/"))
                    }
                }
            } catch (e: IOException) {
                Log.e("TEFModLoader", "错误：" , e)
            }

            LoaderManager.init(
                "${context.getExternalFilesDir(null)}/TEFModLoader/EFModLoaderData/info.json",
                context)

            ModManager.init(
                "${context.getExternalFilesDir(null)}/TEFModLoader/EFModData/info.json",
                context)

            try {
                if (SPUtils.readInt(Settings.jsonPath, 0) == 0) {
                    renameFilesWithOggExtension(File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFModX"))
                    renameFilesWithOggExtension(File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFMod"))
                }
            } catch (e: IOException) {
                Log.e("TEFModLoader", "错误：" , e)
            }
            dismissLoadingDialog()

            
            val launchIntent = context.packageManager.getLaunchIntentForPackage(
                SPUtils.readString(
                Settings.GamePackageName, "com.and.games505.TerrariaPaid").toString())
            if (launchIntent != null) {
                context.startActivity(launchIntent)
            } else {
                EFLog.e("无法找到该应用: $launchIntent")
            }
        }.start()
    }


    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            val circularProgressIndicator = CircularProgressIndicator(context).apply {
                setIndeterminate(true)
                setPadding(15, 15, 15, 15)
            }

            loadingDialog = MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.loadingMod))
                .setView(circularProgressIndicator)
                .setCancelable(true)
                .create()

            // 确保加载框不会因为点击外部区域而消失
            loadingDialog?.setCanceledOnTouchOutside(false)
        }
        loadingDialog?.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    @SuppressLint("SetWorldReadable")
    private fun renameFilesWithOggExtension(directory: File) {
        if (!directory.exists() || !directory.isDirectory) {
            Log.e("GamePanelFragment", "指定的路径不是一个有效的目录: ${directory.absolutePath}")
            return
        }

        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                // 递归处理子目录
                renameFilesWithOggExtension(file)
            } else {
                // 重命名文件，添加 .ogg 扩展名
                val newFileName = "${file.name}.ogg"
                val newFilePath = File(file.parent, newFileName)
                Log.i("GamePanelFragment", "尝试重命名文件: ${file.name} to $newFileName")
                if (file.renameTo(newFilePath)) {
                    // 设置文件为对所有人可读
                    newFilePath.setReadable(true, false)
                    Log.i("GamePanelFragment", "文件重命名成功并设置为可读: ${file.name} -> $newFileName")

                    // 对于 Android 10 及以上版本，使用 MediaStore API
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        addFileToMediaStore(newFilePath)
                    } else {
                        scanFile(newFilePath)
                    }
                } else {
                    Log.e("GamePanelFragment", "文件重命名失败: ${file.name}")
                }
            }
        }
    }

    private fun addFileToMediaStore(file: File) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val uri: Uri? = context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
        if (uri == null) {
            Log.e("GamePanelFragment", "无法插入文件到 MediaStore: ${file.path}")
            return
        }

        context.contentResolver.openFileDescriptor(uri, "w", null)?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { fos ->
                FileInputStream(file).copyTo(fos)
            }
        }

        values.put(MediaStore.MediaColumns.IS_PENDING, 0)
        context.contentResolver.update(uri, values, null, null)
        Log.i("GamePanelFragment", "文件已添加到 MediaStore: ${file.path}, URI: $uri")
    }

    private fun scanFile(file: File) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.path),
            null
        ) { path, uri ->
            Log.i("GamePanelFragment", "文件已扫描: $path, URI: $uri")
        }
    }




    fun copyFilesFromTo(sourceDir: File, destDir: File) {
        if (!sourceDir.exists()) {
            println("源目录不存在: ${sourceDir.absolutePath}")
            return
        }

        if (!destDir.exists()) {
            destDir.mkdirs()
            println("创建目标目录: ${destDir.absolutePath}")
        }

        sourceDir.listFiles()?.forEach { entry ->
            val sourcePath = entry
            val destPath = File(destDir, sourcePath.name)

            if (entry.isDirectory) {
                // 递归复制子目录
                copyFilesFromTo(entry, destPath)
            } else {
                // 复制文件并设置时间戳
                copyFileWithTimestamp(sourcePath, destPath)
            }
        }
    }


    fun copyFilesFromToOgg(sourceDir: File, destDir: File) {
        if (!sourceDir.exists()) {
            println("源目录不存在: ${sourceDir.absolutePath}")
            return
        }

        if (!destDir.exists()) {
            destDir.mkdirs()
            println("创建目标目录: ${destDir.absolutePath}")
        }

        sourceDir.listFiles()?.forEach { entry ->
            val sourcePath = entry
            val destPath: File

            if (entry.isDirectory) {
                // 递归复制子目录
                destPath = File(destDir, entry.name)
                copyFilesFromToOgg(entry, destPath)
            } else {
                // 在文件名后面添加 .ogg 后缀
                destPath = File(destDir, "${entry.name}.ogg")
                // 复制文件并设置时间戳
                copyFileWithTimestamp(sourcePath, destPath)
            }
        }
    }


    @Throws(Exception::class)
    fun syncDirectories(dir1: File, dir2: File) {
        if (!dir1.isDirectory || !dir2.isDirectory) {
            println("Both paths must be directories.")
            return
        }

        // 获取两个目录下的所有文件列表
        val files1 = dir1.listFiles()?.filter { it.isFile }?.toList() ?: emptyList<File>()
        val files2 = dir2.listFiles()?.filter { it.isFile }?.toList() ?: emptyList<File>()

        // 创建文件映射，以便快速查找
        val fileMap1 = HashMap<String, File>()
        for (file in files1) {
            fileMap1[file.name] = file
        }

        val fileMap2 = HashMap<String, File>()
        for (file in files2) {
            fileMap2[file.name] = file
        }

        // 检查dir1中的文件是否需要更新到dir2
        for ((fileName, file) in fileMap1.entries) {
            val otherFile = fileMap2[fileName]
            if (otherFile == null || file.lastModified() > otherFile.lastModified() || file.length() != otherFile.length()) {
                copyFileWithAttributes(file, File(dir2, fileName))
            }
        }

        // 检查dir2中的文件是否需要更新到dir1
        for ((fileName, file) in fileMap2.entries) {
            val otherFile = fileMap1[fileName]
            if (otherFile == null || file.lastModified() > otherFile.lastModified() || file.length() != otherFile.length()) {
                copyFileWithAttributes(file, File(dir1, fileName))
            }
        }

        // 删除dir1中不存在于dir2的文件
        for (file in files1) {
            if (!fileMap2.containsKey(file.name)) {
                file.delete()
                println("删除文件: ${file.absolutePath}")
            }
        }

        // 删除dir2中不存在于dir1的文件
        for (file in files2) {
            if (!fileMap1.containsKey(file.name)) {
                file.delete()
                println("删除文件: ${file.absolutePath}")
            }
        }
    }

    @Throws(Exception::class)
    fun copyFileWithAttributes(source: File, target: File) {
        if (!target.parentFile.exists()) {
            target.parentFile.mkdirs()
        }
        FileInputStream(source).use { input ->
            FileOutputStream(target).use { output ->
                val channelIn: FileChannel = input.channel
                val channelOut: FileChannel = output.channel
                channelIn.transferTo(0, channelIn.size(), channelOut)
            }
        }
        target.setLastModified(source.lastModified())
        println("复制文件: ${source.absolutePath} 到 ${target.absolutePath}")
    }




    @Throws(IOException::class)
    fun copyFileWithTimestamp(sourcePath: File, destPath: File) {
        if (destPath.exists()) {
            // 比较时间戳和文件大小
            val sourceLastWriteTime = sourcePath.lastModified()
            val destLastWriteTime = destPath.lastModified()
            val sourceFileSize = sourcePath.length()
            val destFileSize = destPath.length()

            if (sourceLastWriteTime != destLastWriteTime || sourceFileSize != destFileSize) {
                // 文件不同，执行复制
                try {
                    copyFile(sourcePath, destPath)
                    setLastModifiedTime(destPath, sourceLastWriteTime)
                    println("复制文件: ${sourcePath.absolutePath} 到 ${destPath.absolutePath}")
                } catch (e: IOException) {
                    println("复制文件失败: ${sourcePath.absolutePath} 错误: ${e.message}")
                }
            } else {
                println("文件相同，跳过复制: ${sourcePath.absolutePath}")
            }
        } else {
            try {
                copyFile(sourcePath, destPath)
                setLastModifiedTime(destPath, sourcePath.lastModified())
                println("复制文件: ${sourcePath.absolutePath} 到 ${destPath.absolutePath}")
            } catch (e: IOException) {
                println("复制文件失败: ${sourcePath.absolutePath} 错误: ${e.message}")
            }
        }
    }

    @Throws(IOException::class)
    fun copyFile(source: File, target: File) {
        FileInputStream(source).use { input ->
            FileOutputStream(target).use { output ->
                val channelIn: FileChannel = input.channel
                val channelOut: FileChannel = output.channel
                channelIn.transferTo(0, channelIn.size(), channelOut)
            }
        }
    }

    fun setLastModifiedTime(file: File, lastModifiedTime: Long) {
        file.setLastModified(lastModifiedTime)
    }
}