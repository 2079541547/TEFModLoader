package silkways.terraria.efmodloader.logic.efmod

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import okio.Path
import org.json.JSONArray
import org.json.JSONObject
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.utils.FileUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets

/*******************************************************************************
 * 文件名称: ModManager
 * 项目名称: TEFModLoader
 * 创建时间: 2024/12/21
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
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
 *
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

object ModManager {
    external fun install(inpuPath: String, outPath: String)
    external fun getModInfo(inpuPath: String): ByteArray
    fun parseModInfoToMap(inpuPath: String): Map<String, Any> {
        val jsonString = String(getModInfo(inpuPath), StandardCharsets.UTF_8)
        val jsonObject = JSONObject(jsonString)
        return toMap(jsonObject)
    }

    fun initialization(context: Context) {
        val architecture = when(SPUtils.readString(Settings.architecture, "system")) {
            "arm64-v8a" -> "arm64-v8a"
            "armeabi-v7a" -> "armeabi-v7a"
            else -> when(Build.CPU_ABI){
                "x86_64" -> "arm64-v8a"
                "x86" -> "armeabi-v7a"
                else -> Build.CPU_ABI
            }
        }.toString()

        val modPath = File(context.getExternalFilesDir(null), "EFMod")
        if (modPath.exists()) {
            when (SPUtils.readInt(Settings.Runtime, 0)) {
                0 -> {
                    val tagPath =
                        File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader")
                    try {
                        for (path in modPath.listFiles { file -> file.isDirectory }!!) {
                            if (File(path, "enable").exists()) {
                                if (!File(path, "Modx").exists()) {
                                    FileUtils.deleteDirectory(
                                        File(
                                            tagPath,
                                            "EFMod/Mod/${path.name}/lib"
                                        )
                                    )
                                    copyFilesFromTo(
                                        File(path, "lib/android/$architecture"),
                                        File(tagPath, "EFMod/Mod/${path.name}/lib")
                                    )
                                    copyFilesFromTo(
                                        File(path, "private"),
                                        File(tagPath, "EFMod/Mod/${path.name}/private")
                                    )
                                    syncDirectories(
                                        File(path, "private"),
                                        File(tagPath, "EFMod/Mod/${path.name}/private")
                                    )
                                } else {
                                    FileUtils.deleteDirectory(
                                        File(
                                            tagPath,
                                            "EFMod/Modx/${path.name}/lib"
                                        )
                                    )
                                    copyFilesFromTo(
                                        File(path, "lib/android/$architecture"),
                                        File(tagPath, "EFMod/Modx/${path.name}/lib")
                                    )
                                }
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                1 -> {
                    val packageName = SPUtils.readString(
                        Settings.GamePackageName,
                        "com.and.games505.TerrariaPaid"
                    ).toString()
                    val packageManager: PackageManager = context.packageManager
                    val applicationInfo: ApplicationInfo =
                        packageManager.getApplicationInfo(packageName, 0)
                    val tagPath = File(applicationInfo.dataDir, "cache")

                    if (tagPath.exists()) {
                        for (path in tagPath.listFiles { file -> file.isFile }!!) {
                            path.delete()
                        }
                    } else {
                        tagPath.mkdirs()
                    }

                    for (path in modPath.listFiles { file -> file.isDirectory }!!) {
                        if (File(path, "enable").exists()) {
                            if (!File(path, "Modx").exists()) {
                                try {
                                    FileUtils.deleteDirectory(
                                        File(
                                            tagPath,
                                            "EFMod/Mod/${path.name}/lib"
                                        )
                                    )
                                    copyFilesFromTo(
                                        File(path, "lib/android/$architecture"),
                                        File(tagPath, "EFMod/Mod/${path.name}/lib")
                                    )
                                    copyFilesFromTo(
                                        File(path, "private"),
                                        File(tagPath, "EFMod/Mod/${path.name}/private")
                                    )
                                    syncDirectories(
                                        File(path, "private"),
                                        File(tagPath, "EFMod/Mod/${path.name}/private")
                                    )
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            } else {
                                FileUtils.deleteDirectory(
                                    File(
                                        tagPath,
                                        "EFMod/Modx/${path.name}/lib"
                                    )
                                )
                                copyFilesFromTo(
                                    File(path, "lib/android/$architecture"),
                                    File(tagPath, "EFMod/Modx/${path.name}/lib")
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toMap(jsonObject: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject[key]
            when (value) {
                is JSONObject -> map[key] = toMap(value)
                is JSONArray -> map[key] = toList(value)
                else -> map[key] = value
            }
        }
        return map
    }

    private fun toList(jsonArray: JSONArray): List<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until jsonArray.length()) {
            val value = jsonArray[i]
            when (value) {
                is JSONObject -> list.add(toMap(value))
                is JSONArray -> list.add(toList(value))
                else -> list.add(value)
            }
        }
        return list
    }

    private fun copyFilesFromTo(sourceDir: File, destDir: File) {
        if (!sourceDir.exists()) {
            EFLog.e("源目录不存在: ${sourceDir.absolutePath}")
            return
        }

        if (!destDir.exists()) {
            destDir.mkdirs()
            EFLog.i("创建目标目录: ${destDir.absolutePath}")
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

    @Throws(Exception::class)
    private fun syncDirectories(dir1: File, dir2: File) {
        if (!dir1.isDirectory || !dir2.isDirectory) {
            EFLog.e("Both paths must be directories.")
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
                EFLog.i("删除文件: ${file.absolutePath}")
            }
        }

        // 删除dir2中不存在于dir1的文件
        for (file in files2) {
            if (!fileMap1.containsKey(file.name)) {
                file.delete()
                EFLog.i("删除文件: ${file.absolutePath}")
            }
        }
    }

    @Throws(Exception::class)
    private fun copyFileWithAttributes(source: File, target: File) {
        if (target.parentFile!!.exists()) {
            target.parentFile!!.mkdirs()
        }
        FileInputStream(source).use { input ->
            FileOutputStream(target).use { output ->
                val channelIn: FileChannel = input.channel
                val channelOut: FileChannel = output.channel
                channelIn.transferTo(0, channelIn.size(), channelOut)
            }
        }
        target.setLastModified(source.lastModified())
        EFLog.i("复制文件: ${source.absolutePath} 到 ${target.absolutePath}")
    }




    @Throws(IOException::class)
    private fun copyFileWithTimestamp(sourcePath: File, destPath: File) {
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
                    EFLog.i("复制文件: ${sourcePath.absolutePath} 到 ${destPath.absolutePath}")
                } catch (e: IOException) {
                    EFLog.e("复制文件失败: ${sourcePath.absolutePath} 错误: ${e.message}")
                }
            } else {
                EFLog.i("文件相同，跳过复制: ${sourcePath.absolutePath} -> ${destPath.absolutePath}")
            }
        } else {
            try {
                copyFile(sourcePath, destPath)
                setLastModifiedTime(destPath, sourcePath.lastModified())
                EFLog.i("复制文件: ${sourcePath.absolutePath} 到 ${destPath.absolutePath}")
            } catch (e: IOException) {
                EFLog.e("复制文件失败: ${sourcePath.absolutePath} 错误: ${e.message}")
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(source: File, target: File) {
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