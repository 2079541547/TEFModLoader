package silkways.terraria.efmodloader.logic.mod

import android.content.Context
import android.util.Log
import org.json.JSONObject
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object ModManager {

    fun enableEFMod(context: Context, filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e("EFModLoader", "文件不存在: $filePath")
            return
        }

        try {
            ZipFile(file).use { zip ->
                var config: JSONObject? = null

                // 读取配置文件
                zip.entries().asSequence().filter { it.name == "info.json" }.forEach { entry ->
                    zip.getInputStream(entry).use { input ->
                        val configContent = input.bufferedReader().use { it.readText() }
                        config = JSONObject(configContent)
                    }
                }

                if (config == null) {
                    Log.e("EFModLoader", "无法找到或读取配置文件")
                    return
                }

                val runtime = config.getString("Runtime")
                if (runtime != "cpp") {
                    Log.e("EFModLoader", "不支持的运行时环境: $runtime")
                    return
                }

                // 解压可执行文件
                val targetDir = context.cacheDir.resolve("runEFMod/$runtime")
                targetDir.mkdirs()

                zip.entries().asSequence().filter { it.name.startsWith("Executable/") }.forEach { entry ->
                    val destFile = if (entry.isDirectory) {
                        File(targetDir, entry.name.removePrefix("Executable/"))
                    } else {
                        getUniqueFilePath(targetDir, entry.name.removePrefix("Executable/"))
                    }

                    if (entry.isDirectory) {
                        if (!destFile.exists()) {
                            destFile.mkdirs()
                        }
                    } else {
                        destFile.parentFile?.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            FileOutputStream(destFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        // 调试输出
                        Log.d("EFModLoader", "解压文件: ${entry.name} 到 ${destFile.absolutePath}")
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EFModLoader", "解压过程中发生错误", e)
        }
    }

    private fun getUniqueFilePath(baseDir: File, originalPath: String): File {
        val originalFile = baseDir.resolve(originalPath)
        if (!originalFile.exists()) {
            return originalFile
        }

        val nameWithoutExt = if ("." in originalFile.name) {
            originalFile.name.substringBeforeLast(".")
        } else {
            originalFile.name
        }
        val ext = if ("." in originalFile.name) {
            originalFile.name.substringAfterLast(".")
        } else {
            ""
        }

        var counter = 1
        var uniqueFile: File

        do {
            uniqueFile = if (ext.isNotEmpty()) {
                File(originalFile.parentFile, "${nameWithoutExt}_$counter.$ext")
            } else {
                File(originalFile.parentFile, "${nameWithoutExt}_$counter")
            }
            counter++
        } while (uniqueFile.exists())

        return uniqueFile
    }

    fun removeEFMod(context: Context, filePath: String, runtime: String, identifier: String) {
        JsonConfigModifier.removeKeyFromJson(context, "ToolBoxData/EFModData/info.json", filePath)
        File(filePath).delete()

        deleteDirectory(File("${context.getExternalFilesDir(null)}/EFMod-Private/$runtime/$identifier"))
    }

    private fun deleteDirectory(directory: File) {
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    file.delete()
                }
            }
            directory.delete()
        }
    }
}