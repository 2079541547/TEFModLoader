package silkways.terraria.efmodloader.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.logic.EFLog
import java.io.*

class FileUtils {
    companion object {

        @SuppressLint("StaticFieldLeak")
        private val context: Context = MainApplication.getContext()

        fun checkAndWriteFile() {
            val content = "[]"
            val file = File("${context.getExternalFilesDir(null)}/TEFModLoader/EFModData/info.json")

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

        /**
         * 从 URI 获取文件的真实路径。
         *
         * @param contentUri 文件的 URI
         * @return 文件的真实路径
         */
        fun getRealPathFromURI(contentUri: Uri, context: Context): String? {
            return try {
                context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                    // 创建缓存目录
                    val cacheDir = File(context.cacheDir, "temp")
                    if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                        EFLog.e("创建缓存目录失败: ${cacheDir.absolutePath}")
                        return null
                    }

                    // 获取文件名
                    val fileName = getFileNameFromURI(contentUri, context)
                    if (fileName.isEmpty()) {
                        EFLog.e("无法获取文件名")
                        return null
                    }

                    // 创建临时文件
                    val tempFile = File(cacheDir, fileName)
                    tempFile.writeBytes(inputStream.readBytes())
                    EFLog.d("文件保存成功: ${tempFile.absolutePath}")
                    tempFile.absolutePath
                } ?: run {
                    EFLog.e("无法打开输入流: $contentUri")
                    null
                }
            } catch (e: IOException) {
                EFLog.e("从 URI 获取文件真实路径时发生 IO 异常: ${e.message}")
                e.printStackTrace()
                null
            } catch (e: Exception) {
                EFLog.e("从 URI 获取文件真实路径时发生异常: ${e.message}")
                e.printStackTrace()
                null
            }
        }

        /**
         * 从 URI 中获取文件名。
         *
         * @param uri 文件的 URI
         * @return 文件名
         */
        private fun getFileNameFromURI(uri: Uri, context: Context): String {
            return try {
                val contentResolver: ContentResolver = context.contentResolver
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val fileName = cursor.getString(nameIndex)
                        EFLog.d("从 URI 获取文件名成功: $fileName")
                        fileName
                    } else {
                        EFLog.w("Cursor 为空或没有移动到第一个位置")
                        ""
                    }
                } ?: run {
                    EFLog.e("查询 URI 失败: $uri")
                    ""
                }
            } catch (e: Exception) {
                EFLog.e("从 URI 获取文件名时发生异常: ${e.message}")
                e.printStackTrace()
                ""
            }
        }


        /**
         * 复制文件，可以选择是否覆盖目标文件。
         *
         * @param sourcePath 源文件路径
         * @param destinationPath 目标文件路径
         * @param overwrite 是否覆盖目标文件
         */
        fun copyFile(sourcePath: String?, destinationPath: String?, overwrite: Boolean) {
            val sourceFile = sourcePath?.let { File(it) } ?: run {
                EFLog.e("源文件路径为空")
                return
            }
            val destFile = destinationPath?.let { File(it) } ?: run {
                EFLog.e("目标文件路径为空")
                return
            }

            // 检查源文件是否存在
            if (!sourceFile.exists()) {
                EFLog.e("源文件不存在: ${sourceFile.absolutePath}")
                return
            }

            // 检查目标文件是否存在
            if (destFile.exists()) {
                if (overwrite) {
                    // 如果目标文件存在且需要覆盖，则删除目标文件
                    if (destFile.delete()) {
                        EFLog.d("目标文件删除成功: ${destFile.absolutePath}")
                    } else {
                        EFLog.e("目标文件删除失败: ${destFile.absolutePath}")
                        return
                    }
                } else {
                    // 如果目标文件存在且不需要覆盖，则直接返回
                    EFLog.i("目标文件已存在，不进行复制: ${destFile.absolutePath}")
                    return
                }
            }

            try {
                // 使用 FileInputStream 和 FileOutputStream 进行文件复制
                FileInputStream(sourceFile).use { fis ->
                    FileOutputStream(destFile).use { fos ->
                        fis.channel.use { inputChannel ->
                            fos.channel.use { outputChannel ->
                                // 直接使用 FileChannel 进行高效复制
                                inputChannel.transferTo(0, inputChannel.size(), outputChannel)
                                EFLog.d("文件复制成功: ${sourceFile.absolutePath} -> ${destFile.absolutePath}")
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                EFLog.e("文件复制过程中发生异常: ${e.message}")
                e.printStackTrace()
            }
        }


        /**
         * 删除指定目录及其所有子目录和文件。
         *
         * @param directory 要删除的目录对象
         */
        fun deleteDirectory(directory: File) {
            // 检查目录是否存在
            if (directory.exists()) {
                EFLog.i("开始删除目录: ${directory.absolutePath}")
                try {
                    // 获取目录下的所有文件和子目录列表
                    directory.listFiles()?.forEach { file ->
                        try {
                            // 如果当前项是目录，则递归调用 deleteDirectory 方法
                            if (file.isDirectory) {
                                EFLog.i("进入子目录: ${file.absolutePath}")
                                deleteDirectory(file)
                            } else {
                                // 如果当前项是文件，则尝试删除
                                if (file.delete()) {
                                    EFLog.i("成功删除文件: ${file.absolutePath}")
                                } else {
                                    EFLog.e("无法删除文件: ${file.absolutePath}")
                                }
                            }
                        } catch (e: Exception) {
                            EFLog.e("删除过程中发生异常: ${file.absolutePath}, 异常信息: ${e.message}")
                        }
                    }
                    // 尝试删除空目录本身
                    if (directory.delete()) {
                        EFLog.i("成功删除目录: ${directory.absolutePath}")
                    } else {
                        EFLog.e("无法删除目录: ${directory.absolutePath}")
                    }
                } catch (e: IOException) {
                    EFLog.e("访问目录时发生IO异常: ${directory.absolutePath}, 异常信息: ${e.message}")
                } finally {
                    EFLog.i("完成删除操作: ${directory.absolutePath}")
                }
            } else {
                EFLog.w("指定的目录不存在: ${directory.absolutePath}")
            }
        }
    }
}
