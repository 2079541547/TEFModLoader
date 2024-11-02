package silkways.terraria.efmodloader.utils

import android.annotation.SuppressLint
import android.content.Context
import silkways.terraria.efmodloader.MainApplication
import java.io.*

class FileUtils {
    companion object {

        @SuppressLint("StaticFieldLeak")
        private val context: Context = MainApplication.getContext()

        fun checkAndWriteFile() {
            val content = "[]"
            val file = File("${context.getExternalFilesDir(null)}/ToolBoxData/ModData/mod_data.json")

            if (!file.exists()) {
                try {
                    file.createNewFile()
                    file.writeText(content)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        @Throws(IOException::class)
        fun readFileFromAssets(fileName: String): String {
            val stringBuilder = StringBuilder()
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }

            reader.close()
            inputStream.close()

            return stringBuilder.toString()
        }

        fun copyFileIfNotExists(sourcePath: String, destinationPath: String) {
            val sourceFile = File(sourcePath)
            val destFile = File(destinationPath)
            // 检查目标文件是否已经存在
            if (destFile.exists()) {
                println("文件已存在")
                // 文件已存在，不做任何操作
                return
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

        fun clearCache() {
            // 清除应用的内部缓存目录
            val cacheDir = context.cacheDir
            // 清除应用的外部缓存目录（如果存在）
            val externalCacheDir = context.externalCacheDir

            deleteDirectory(cacheDir)
            if (externalCacheDir != null) {
                deleteDirectory(externalCacheDir)
            }
        }


        fun deleteDirectory(directory: File) {
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
}
