package silkways.terraria.efmodloader.logic.mod

import android.content.Context
import android.content.DialogInterface
import android.os.Environment
import android.util.Log
import org.json.JSONObject
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

object ModJsonManager {

    // 移动文件并更新配置
    fun moveFileAndUpdateConfig(context: Context, fromPath: String, toDirectory: String, zipFileName: String) {
        val sourceFile = File(fromPath, zipFileName)
        val targetDir = File(toDirectory)
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }
        val targetFile = File(targetDir, zipFileName)

        Log.d("MoveFile", "Source file: ${sourceFile.absolutePath}")
        Log.d("MoveFile", "Target directory: ${targetDir.absolutePath}")
        Log.d("MoveFile", "Target file: ${targetFile.absolutePath}")

        if (targetFile.exists()) {
            showConfirmationDialog(context, zipFileName) { shouldReplace ->
                if (shouldReplace) {
                    copyFileOverwritingExisting(sourceFile.absolutePath, targetFile.absolutePath)
                    updateJsonConfig(context, targetDir, targetFile.toString())
                    extractBasedOnRuntime(context, targetDir, zipFileName)
                }
            }
        } else {
            copyFileOverwritingExisting(sourceFile.absolutePath, targetFile.absolutePath)
            updateJsonConfig(context, targetDir, targetFile.toString())
            extractBasedOnRuntime(context, targetDir, zipFileName)
        }
    }

    // 更新或创建 JSON 配置
    private fun updateJsonConfig(context: Context, targetDir: File, zipFileName: String) {
        val configFilePath = File(targetDir, "info.json")
        val configExists = configFilePath.exists()

        val jsonObject = if (configExists) {
            JSONObject(FileReader(configFilePath).readText()).apply {
                Log.d("JSONContent", "现有 JSON 内容: $this")
            }
        } else {
            JSONObject().apply {
                Log.d("JSONContent", "新创建的 JSON 内容: $this")
            }
        }

        if (jsonObject.has(zipFileName)) {
            showConfirmationDialog(context, zipFileName) { shouldReplace ->
                if (shouldReplace) {
                    jsonObject.put(zipFileName, false)
                    writeJsonToFile(jsonObject, configFilePath)
                }
            }
        } else {
            jsonObject.put(zipFileName, false)
            writeJsonToFile(jsonObject, configFilePath)
        }

    }

    // 写 JSON 到文件
    private fun writeJsonToFile(jsonObject: JSONObject, file: File) {
        val writer = FileWriter(file)
        writer.write(jsonObject.toString(4))
        writer.close()
    }

    // 解压 Private 目录下的文件
    private fun unzipPrivateFiles(targetDir: File, zipFile: File) {
        if (!zipFile.exists()) {
            Log.e("ZipFileNotFound", "Zip file not found: ${zipFile.absolutePath}")
            return
        }

        val zipInputStream = ZipInputStream(BufferedInputStream(FileInputStream(zipFile)))
        var entry: ZipEntry?

        while (zipInputStream.nextEntry.also { entry = it } != null) {
            if (entry?.name?.startsWith("Private/") == true) {
                val entryFileName = entry.name
                val outputFilePath = File(targetDir, entryFileName.replaceFirst("Private/", ""))

                if (entry.isDirectory) {
                    outputFilePath.mkdirs()
                } else {
                    val parentDir = outputFilePath.parentFile
                    if (!parentDir.exists()) {
                        parentDir.mkdirs()
                    }

                    if (outputFilePath.exists()) {
                        Log.d("FileExists", "File already exists: ${outputFilePath.absolutePath}")
                    } else {
                        val outputStream = FileOutputStream(outputFilePath)
                        zipInputStream.copyTo(outputStream)
                        outputStream.close()
                        Log.d("FileExtracted", "File extracted: ${outputFilePath.absolutePath}")
                    }
                }
            }
        }
        zipInputStream.close()
    }

    // 显示确认对话框
    private fun showConfirmationDialog(context: Context, message: String, onConfirm: (Boolean) -> Unit) {
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
        dialogBuilder.setMessage("文件 $message 已存在，是否要替换？")
            .setCancelable(false)
            .setPositiveButton("确定") { _: DialogInterface, _: Int ->
                onConfirm(true)
            }
            .setNegativeButton("取消") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                onConfirm(false)
            }
        val alert = dialogBuilder.create()
        alert.setTitle("确认替换？")
        alert.show()
    }

    // 读取 info.json 文件
    fun readInfoJsonFromZip(zipFilePath: String): JSONObject? {
        val zipFile = ZipFile(File(zipFilePath))
        val entries = zipFile.entries()

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.name == "info.json") {
                val inputStream = zipFile.getInputStream(entry)
                val reader = InputStreamReader(inputStream, "UTF-8")
                val content = reader.readText()
                reader.close()
                zipFile.close()
                return JSONObject(content)
            }
        }

        Log.e("InfoJsonNotFound", "info.json 文件在 ZIP 文件中未找到: $zipFilePath")
        zipFile.close()
        return null
    }

    // 根据 Runtime 类型解压到不同路径
    fun extractBasedOnRuntime(context: Context, targetDir: File, zipFileName: String) {
        val infoJson = readInfoJsonFromZip("$targetDir/$zipFileName") ?: return
        val runtime = infoJson.optString("Runtime", "default_runtime") // 提供默认值
        val identifier = infoJson.optString("Identifier", "default_identifier") // 提供默认值

        val runtimeDir = File(context.getExternalFilesDir(null), "EFMod-Private/$runtime/")
        if (!runtimeDir.exists()) {
            runtimeDir.mkdirs()
        }

        val modDir = File(runtimeDir, identifier)
        if (!modDir.exists()) {
            modDir.mkdirs()
        }

        val zipFilePath = File(context.getExternalFilesDir(null), "ToolBoxData/EFModData/$zipFileName")
        if (!zipFilePath.exists()) {
            Log.e("ZipFileNotFound", "Zip file not found: ${zipFilePath.absolutePath}")
            return
        }

        unzipPrivateFiles(modDir, zipFilePath)
    }

    private fun copyFileOverwritingExisting(sourcePath: String?, destinationPath: String?) {
        val sourceFile = sourcePath?.let { File(it) }
        val destFile = destinationPath?.let { File(it) }

        // 删除文件
        if (destFile != null) {
            if (destFile.exists()) {
                if (destFile.delete()) {
                    Log.d("FileDeleted", "文件删除成功: ${destFile.absolutePath}")
                } else {
                    Log.e("FileDeleteError", "文件删除失败: ${destFile.absolutePath}")
                }
            } else {
                Log.i("FileNotFound", "文件不存在: ${destFile.absolutePath}")
            }
        }

        try {
            FileInputStream(sourceFile).use { fis ->
                FileOutputStream(destFile).use { fos ->
                    fis.channel.use { inputChannel ->
                        fos.channel.use { outputChannel ->
                            // 直接使用FileChannel进行高效复制
                            inputChannel.transferTo(0, inputChannel.size(), outputChannel)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
