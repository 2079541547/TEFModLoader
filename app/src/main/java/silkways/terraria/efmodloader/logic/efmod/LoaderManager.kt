/*******************************************************************************
 * 文件名称: LoaderManager
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

package silkways.terraria.efmodloader.logic.efmod

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
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


object LoaderManager {
        external fun install(inpuPath: String, outPath: String)
        external fun getLoaderInfo(inpuPath: String): ByteArray

        fun parseLoaderInfoToMap(inpuPath: String): Map<String, Any> {
                val jsonString = String(getLoaderInfo(inpuPath), StandardCharsets.UTF_8)
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

                var loaderPath = try {
                                File(JSONObject(File("${context.getExternalFilesDir(null)}/EFModLoader/info.json").readText()).getString("selectedLoaderPath"), "lib/android/$architecture")
                        } catch (e: IOException) {
                                e.printStackTrace()
                                File("")
                        }

                when (SPUtils.readInt(Settings.Runtime, 0)) {
                        0 -> {
                                val tagPath = File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader/Loader")
                                if (tagPath.exists()) {
                                        for (path in tagPath.listFiles { file -> file.isFile }!!) {
                                                path.delete()
                                        }
                                } else {
                                        tagPath.mkdirs()
                                }

                                if (loaderPath.exists()) {
                                        for (path in loaderPath.listFiles { file -> file.isFile }!!) {
                                                FileUtils.copyFile(
                                                        path.path,
                                                        "$tagPath/${path.name}",
                                                        true
                                                )
                                        }
                                }
                        }

                        1 -> {
                                val packageName = SPUtils.readString(Settings.GamePackageName, "com.and.games505.TerrariaPaid").toString()
                                val packageManager: PackageManager = context.packageManager
                                val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
                                val gameCache = File(applicationInfo.dataDir, "cache").path
                                val tagPath = File(gameCache, "Loader")

                                if (tagPath.exists()) {
                                        for (path in tagPath.listFiles { file -> file.isFile }!!) {
                                                path.delete()
                                        }
                                } else {
                                        tagPath.mkdirs()
                                }

                                if (loaderPath.exists()) {
                                        for (path in loaderPath.listFiles { file -> file.isFile }!!) {
                                                FileUtils.copyFile(
                                                        path.path,
                                                        "$tagPath/${path.name}",
                                                        true
                                                )
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
}