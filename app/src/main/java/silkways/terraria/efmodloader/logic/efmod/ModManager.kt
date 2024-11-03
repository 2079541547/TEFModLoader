package silkways.terraria.efmodloader.logic.efmod

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import eternal.future.effsystem.fileSystem
import org.json.JSONObject
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.data.TEFModLoader
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.efmod.Utils.showConfirmationDialog
import silkways.terraria.efmodloader.utils.FileUtils
import java.io.File
import java.io.FileReader
import java.io.IOException

/*******************************************************************************
 * 文件名称: ModManager
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/3 01:49
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

object ModManager {

    /**
     * 安装Mod文件到目标目录。
     *
     * @param context 上下文
     * @param modFile Mod文件
     * @param targetDir 目标目录
     */
    fun install(context: Context, modFile: File, targetDir: File) {
        // 确保目标目录存在
        if (!targetDir.exists()) {
            targetDir.mkdirs()
            EFLog.i("目标目录不存在，已创建: ${targetDir.absolutePath}")
        }

        val targetFile = File("$targetDir/${modFile.name}")

        if (targetFile.exists()) {
            // 如果目标文件已存在，显示确认对话框
            showConfirmationDialog(context, targetFile.absolutePath) { shouldReplace ->
                if (shouldReplace) {
                    // 复制Mod文件并覆盖目标文件
                    FileUtils.copyFile(modFile.absolutePath, targetFile.absolutePath, true)
                    handleModFile(targetFile)
                    EFLog.i("已覆盖并安装Mod文件: ${targetFile.absolutePath}")
                } else {
                    EFLog.d("用户选择不覆盖现有Mod文件: ${targetFile.absolutePath}")
                }
            }
        } else {
            // 如果目标文件不存在，直接安装Mod文件
            val configFilePath = File(targetDir, "info.json")
            val configExists = configFilePath.exists()

            val jsonObject = if (configExists) {
                JSONObject(FileReader(configFilePath).readText())
            } else {
                JSONObject()
            }

            // 更新info.json文件，将新的Mod文件路径添加进去
            jsonObject.put(targetFile.absolutePath, false)
            Utils.writeJsonToFile(jsonObject, configFilePath)

            // 复制Mod文件到目标位置
            FileUtils.copyFile(modFile.absolutePath, targetFile.absolutePath, true)
            handleModFile(targetFile)
            EFLog.i("Mod文件已成功安装: ${targetFile.absolutePath}")
        }
    }


    /**
     * 初始化Mod文件。
     *
     * @param filePath Mod配置文件的路径
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
                        EFLog.d("处理Mod文件: $key")

                        when (JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.Runtime)) {
                            0 -> {
                                if (fileSystem.EFMC.getModInfo(key)["SpecialLoading"] as Boolean) {
                                    fileSystem.EFMC.extractExecutable(
                                        key,
                                        Build.CPU_ABI,
                                        "/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFModX/"
                                    )
                                    EFLog.i("特殊加载Mod文件到: /sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFModX/, Key: $key")
                                } else {
                                    fileSystem.EFMC.extractExecutable(
                                        key,
                                        Build.CPU_ABI,
                                        "/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFMod/"
                                    )
                                    EFLog.i("常规加载Mod文件到: /sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFMod/, Key: $key")
                                }
                            }

                            1 -> {
                                val gamePackageName = JsonConfigModifier.readJsonValue(
                                    context,
                                    Settings.jsonPath,
                                    Settings.GamePackageName
                                ) as String

                                if (fileSystem.EFMC.getModInfo(key)["SpecialLoading"] as Boolean) {
                                    fileSystem.EFMC.extractExecutable(
                                        key,
                                        Build.CPU_ABI,
                                        "data/data/$gamePackageName/cache/EFModX/"
                                    )
                                    EFLog.i("特殊加载Mod文件到: data/data/$gamePackageName/cache/EFModX/, Key: $key")
                                } else {
                                    fileSystem.EFMC.extractExecutable(
                                        key,
                                        Build.CPU_ABI,
                                        "data/data/$gamePackageName/cache/EFMod/"
                                    )
                                    EFLog.i("常规加载Mod文件到: data/data/$gamePackageName/cache/EFMod/, Key: $key")
                                }
                            }

                            else -> {
                                EFLog.w("未知的Runtime配置值: ${JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.Runtime)}")
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                // 捕获并记录IO异常
                EFLog.e("读取或解析配置文件时发生错误: $filePath, 错误信息: ${e.message}")
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
     * 卸载Mod文件及其相关资源。
     *
     * @param context 上下文
     * @param filePath Mod文件路径
     * @param identifier Mod的唯一标识符
     */
    fun remove(context: Context, filePath: File, identifier: String) {
        try {
            // 从info.json中移除Mod文件路径
            val jsonFilePath = "${filePath.parent}/info.json"
            JsonConfigModifier.removeKeyFromJson(context, jsonFilePath, filePath.absolutePath)
            EFLog.i("已从info.json中移除Mod文件路径: $filePath")

            if (filePath.exists()) {
                if (filePath.delete()) {
                    EFLog.i("Mod文件已成功删除: $filePath")
                } else {
                    EFLog.e("无法删除Mod文件: $filePath")
                }
            } else {
                EFLog.w("Mod文件不存在: $filePath")
            }

            // 删除私有资源目录
            val privateDirPath = "${filePath.parent}/EFMod-Private/$identifier"
            val privateDir = File(privateDirPath)
            if (privateDir.exists()) {
                FileUtils.deleteDirectory(privateDir)
                EFLog.i("私有资源目录已成功删除: $privateDirPath")
            } else {
                EFLog.w("私有资源目录不存在: $privateDirPath")
            }

        } catch (e: Exception) {
            // 捕获并记录所有异常
            EFLog.e("卸载Mod时发生错误: ${e.message}")
            e.printStackTrace()
        }
    }



    /**
     * 处理Mod文件，包括提取私有资源等操作。
     *
     * @param targetFile 目标文件
     */
    private fun handleModFile(targetFile: File) {
        // 获取Mod信息
        val info = fileSystem.EFMC.getModInfo(targetFile.absolutePath)
        val isPrivate = info["enablePrivate"] as Boolean

        if (isPrivate) {
            // 如果启用私有资源，提取私有资源
            val id = info["identifier"].toString()
            fileSystem.EFMC.extractPrivate(targetFile.absolutePath, "${targetFile.parent}/EFMod-Private/$id")
            EFLog.i("已提取私有资源到: ${targetFile.parent}/EFMod-Private/$id")
        } else {
            EFLog.d("此Mod未启用私有资源: ${targetFile.absolutePath}")
        }
    }

}