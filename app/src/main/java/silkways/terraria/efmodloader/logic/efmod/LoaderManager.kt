package silkways.terraria.efmodloader.logic.efmod

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import eternal.future.effsystem.fileSystem
import org.json.JSONObject
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.efmod.Utils.showConfirmationDialog
import silkways.terraria.efmodloader.utils.FileUtils
import java.io.File
import java.io.FileReader
import java.io.IOException
import org.json.JSONException
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.data.TEFModLoader
import silkways.terraria.efmodloader.utils.SPUtils

/*******************************************************************************
 * 文件名称: LoaderManager
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/3 上午8:47
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

object LoaderManager {

    /**
     * 安装Loader文件到目标目录。
     *
     * @param context 上下文
     * @param loaderFile Loader文件
     * @param targetDir 目标目录
     */
    fun install(context: Context, loaderFile: File, targetDir: File) {
        // 确保目标目录存在
        if (!targetDir.exists()) {
            targetDir.mkdirs()
            EFLog.i("目标目录不存在，已创建: ${targetDir.absolutePath}")
        }

        val targetFile = File("$targetDir/${loaderFile.name}")

        if (targetFile.exists()) {
            // 如果目标文件已存在，显示确认对话框
            showConfirmationDialog(context, targetFile.absolutePath) { shouldReplace ->
                if (shouldReplace) {
                    // 复制Loader文件并覆盖目标文件
                    FileUtils.copyFile(loaderFile.absolutePath, targetFile.absolutePath, true)
                    EFLog.i("已覆盖并安装Loader文件: ${targetFile.absolutePath}")
                } else {
                    EFLog.d("用户选择不覆盖现有Loader文件: ${targetFile.absolutePath}")
                }
            }
        } else {
            // 如果目标文件不存在，直接安装Loader文件
            val configFilePath = File(targetDir, "info.json")
            val configExists = configFilePath.exists()

            val jsonObject = if (configExists) {
                JSONObject(FileReader(configFilePath).readText())
            } else {
                JSONObject()
            }

            // 更新info.json文件，将新的Loader文件路径添加进去
            jsonObject.put(targetFile.absolutePath, false)
            Utils.writeJsonToFile(jsonObject, configFilePath)

            // 复制Loader文件到目标位置
            FileUtils.copyFile(loaderFile.absolutePath, targetFile.absolutePath, true)
            EFLog.i("Loader文件已成功安装: ${targetFile.absolutePath}")
        }
    }


    /**
     * 初始化Loader文件。
     *
     * @param filePath Loader配置文件的路径
     * @param context 应用上下文
     */
    @SuppressLint("SdCardPath")
    fun init(filePath: String, context: Context) {
        val file = File(filePath)
        if (file.exists()) {
            try {
                // 读取文件内容
                val bufferedReader = file.bufferedReader()
                val jsonString = bufferedReader.use { it.readText() }
                EFLog.i("成功读取配置文件: $filePath")

                // 解析JSON
                val jsonObject = JSONObject(jsonString)
                EFLog.i("成功解析JSON配置文件")

                // 遍历JSON对象
                val iterator = jsonObject.keys()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    if (jsonObject.getBoolean(key)) {
                        if (!File(key).exists()) return
                        EFLog.d("处理Loader文件: $key")
                        when (SPUtils.readInt(Settings.jsonPath, 0)) {
                            0 -> {
                                fileSystem.EFML.extractLoader(
                                    key,
                                    when(SPUtils.readString("architecture", Build.CPU_ABI)) {
                                        "x86" -> "armeabi-v7a"
                                        "x86_64" -> "arm64-v8a"
                                        else -> SPUtils.readString("architecture", Build.CPU_ABI)
                                    }.toString(),
                                    "/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/kernel"
                                )
                                EFLog.i("提取Loader到: /sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/kernel, Key: $key")
                            }

                            1 -> {
                                val gamePackageName = JsonConfigModifier.readJsonValue(
                                    context,
                                    Settings.jsonPath,
                                    Settings.GamePackageName
                                ) as String

                                fileSystem.EFML.extractLoader(
                                    key,
                                    when(SPUtils.readString("architecture", Build.CPU_ABI)) {
                                        "x86" -> "armeabi-v7a"
                                        "x86_64" -> "arm64-v8a"
                                        else -> SPUtils.readString("architecture", Build.CPU_ABI)
                                    }.toString(),
                                    "data/data/$gamePackageName/cache/EFModLoader"
                                )
                                EFLog.i("提取Loader到: data/data/$gamePackageName/cache/EFModLoader, Key: $key")
                            }

                            else -> {
                                EFLog.w("未知的Runtime配置值: ${SPUtils.readInt(Settings.jsonPath, 0)}")
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                // 捕获并记录IO异常
                EFLog.e("读取或解析配置文件时发生错误: $filePath, 错误信息: ${e.message}")
            } catch (e: JSONException) {
                // 捕获并记录JSON解析异常
                EFLog.e("解析JSON配置文件时发生错误: $filePath, 错误信息: ${e.message}")
            } catch (e: Exception) {
                // 捕获并记录其他异常
                EFLog.e("初始化Mod文件时发生错误: $filePath, 错误信息: ${e.message}")
                e.printStackTrace()
            }
        } else {
            // 记录文件不存在的错误
            EFLog.e("配置文件不存在: $filePath")
        }
    }


    /**
     * 卸载Loader文件及其相关资源。
     *
     * @param context 应用上下文
     * @param filePath Loader文件的路径
     */
    fun remove(context: Context, filePath: File) {
        try {
            // 检查文件是否存在
            if (filePath.exists()) {
                // 获取info.json文件的路径
                val infoJsonPath = "TEFModLoader/EFModLoaderData/info.json"
                val infoFile = File(infoJsonPath)

                // 检查info.json文件是否存在
                if (infoFile.exists()) {
                    // 从info.json中移除Loader文件路径
                    JsonConfigModifier.removeKeyFromJson(context, infoJsonPath, filePath.absolutePath)
                    EFLog.i("已从info.json中移除Loader文件路径: ${filePath.absolutePath}")
                } else {
                    EFLog.w("info.json文件不存在: $infoJsonPath")
                }

                // 删除Loader文件
                if (filePath.delete()) {
                    EFLog.i("Loader文件已成功删除: ${filePath.absolutePath}")
                } else {
                    EFLog.e("无法删除Loader文件: ${filePath.absolutePath}")
                }
            } else {
                EFLog.w("Loader文件不存在: ${filePath.absolutePath}")
            }
        } catch (e: JSONException) {
            // 捕获并记录JSON解析异常
            EFLog.e("解析info.json时发生错误: ${e.message}")
        } catch (e: IOException) {
            // 捕获并记录IO异常
            EFLog.e("读取或写入info.json时发生错误: ${e.message}")
        } catch (e: Exception) {
            // 捕获并记录其他异常
            EFLog.e("卸载Loader时发生错误: ${e.message}")
            e.printStackTrace()
        }
    }
}