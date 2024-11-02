package silkways.terraria.efmodloader.logic

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/*******************************************************************************
 * 文件名称: FileManager
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/3 01:52
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

object FileManager {

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
                EFLog.e("查询 URI 失败: $uri", "读取布尔值失败: $key, ${e.message}")
                ""
            }
        } catch (e: Exception) {
            EFLog.e(
                "从 URI 获取文件名时发生异常: ${e.message}",
                "读取布尔值失败: $key, ${e.message}"
            )
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
            EFLog.e("源文件路径为空", "读取布尔值失败: $key, ${e.message}")
            return
        }
        val destFile = destinationPath?.let { File(it) } ?: run {
            EFLog.e("目标文件路径为空", "读取布尔值失败: $key, ${e.message}")
            return
        }

        // 检查源文件是否存在
        if (!sourceFile.exists()) {
            EFLog.e(
                "源文件不存在: ${sourceFile.absolutePath}",
                "读取布尔值失败: $key, ${e.message}"
            )
            return
        }

        // 检查目标文件是否存在
        if (destFile.exists()) {
            if (overwrite) {
                // 如果目标文件存在且需要覆盖，则删除目标文件
                if (destFile.delete()) {
                    EFLog.d("目标文件删除成功: ${destFile.absolutePath}")
                } else {
                    EFLog.e(
                        "目标文件删除失败: ${destFile.absolutePath}",
                        "读取布尔值失败: $key, ${e.message}"
                    )
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
            EFLog.e("文件复制过程中发生异常: ${e.message}", "读取布尔值失败: $key, ${e.message}")
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
                                EFLog.e(
                                    "无法删除文件: ${file.absolutePath}",
                                    "读取布尔值失败: $key, ${e.message}"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        EFLog.e(
                            "删除过程中发生异常: ${file.absolutePath}, 异常信息: ${e.message}",
                            "读取布尔值失败: $key, ${e.message}"
                        )
                    }
                }
                // 尝试删除空目录本身
                if (directory.delete()) {
                    EFLog.i("成功删除目录: ${directory.absolutePath}")
                } else {
                    EFLog.e(
                        "无法删除目录: ${directory.absolutePath}",
                        "读取布尔值失败: $key, ${e.message}"
                    )
                }
            } catch (e: IOException) {
                EFLog.e(
                    "访问目录时发生IO异常: ${directory.absolutePath}, 异常信息: ${e.message}",
                    "读取布尔值失败: $key, ${e.message}"
                )
            } finally {
                EFLog.i("完成删除操作: ${directory.absolutePath}")
            }
        } else {
            EFLog.w("指定的目录不存在: ${directory.absolutePath}")
        }
    }


}