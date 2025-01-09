/*******************************************************************************
 * 文件名称: languageUtils
 * 项目名称: TEFModLoader-Compose
 * 创建时间: 2024/11/15 下午11:17
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
 * 描述信息: 本文件为TEFModLoader-Compose项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/


package silkways.terraria.efmodloader.ui.utils

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import silkways.terraria.efmodloader.logic.EFLog
import java.io.IOException
import java.io.InputStream

class LanguageUtils(private val context: Context, private val language: String, private val assetsPath: String) {
    companion object {
        private const val ASSETS_FOLDER = "TEFModLoader/language/"
        private lateinit var cachedJson: JSONObject
    }

    fun loadJsonFromAsset(): JSONObject {
        val fileName = "$ASSETS_FOLDER$language.json"
        EFLog.d("正在打开文件: $fileName")

        return try {
            // 尝试打开指定语言的JSON文件
            val inputStream: InputStream = context.assets.open(fileName)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            EFLog.d("已加载JSON字符串: $jsonString")
            val json = JSONObject(jsonString)

            cachedJson = json // 缓存JSON对象
            json
        } catch (e: IOException) {
            EFLog.e("加载文件失败: $fileName, 使用默认文件: zh-cn.json\n$e")
            // 如果指定语言的文件加载失败，则尝试加载默认的zh-cn.json文件
            val defaultFileName = ASSETS_FOLDER + "zh-cn.json"
            val defaultInputStream: InputStream = context.assets.open(defaultFileName)
            val defaultJsonString = defaultInputStream.bufferedReader().use { it.readText() }
            EFLog.d("已加载默认JSON字符串: $defaultJsonString")
            val defaultJson = JSONObject(defaultJsonString)

            cachedJson = defaultJson // 缓存默认JSON对象
            defaultJson
        } catch (e: JSONException) {
            EFLog.e("解析JSON失败: $e")
            throw RuntimeException("无法解析JSON文件", e)
        }
    }

    fun getString(code: String): String {
        val json = cachedJson
        val assetsObject = json.getJSONObject(assetsPath)
        val result = assetsObject.optString(code, "Unable_to_retrieve_text")
        EFLog.d("成功获取代码 '$code' 对应的文本: $result")
        return result
    }

    fun getString(code: String, code2: String): String {
        val json = cachedJson
        val assetsObject = json.getJSONObject(assetsPath)
        val subObject = assetsObject.optJSONObject(code)
        if (subObject == null) {
            EFLog.e("代码 '$code' 对应的子对象不存在")
            return "Unable_to_retrieve_text"
        }
        val result = subObject.optString(code2, "Unable_to_retrieve_text")
        if (result.isEmpty()) {
            EFLog.e("代码 '$code2' 在子对象 '$code' 中对应的文本为空")
            return "Unable_to_retrieve_text"
        }
        EFLog.d("成功获取代码 '$code2' 和 '$code' 对应的文本: $result")
        return result
    }

    fun getString(code: String, code2: String, code3: String): String {
        val json = cachedJson
        val assetsObject = json.getJSONObject(assetsPath)
        val subObject = assetsObject.optJSONObject(code)
        if (subObject == null) {
            EFLog.e("代码 '$code' 对应的子对象不存在")
            return "Unable_to_retrieve_text"
        }
        val deeperSubObject = subObject.optJSONObject(code2)
        if (deeperSubObject == null) {
            EFLog.e("代码 '$code2' 在子对象 '$code' 中对应的子对象不存在")
            return "Unable_to_retrieve_text"
        }
        val result = deeperSubObject.optString(code3, "Unable_to_retrieve_text")
        if (result.isEmpty()) {
            EFLog.e("代码 '$code3' 在子对象 '$code2' 和 '$code' 中对应的文本为空")
            return "Unable_to_retrieve_text"
        }
        EFLog.d("成功获取代码 '$code3'、'$code2' 和 '$code' 对应的文本: $result")
        return result
    }

    fun getString(code: String, code2: String, code3: String, code4: String): String {
        val json = cachedJson

        val assetsObject = json.getJSONObject(assetsPath)
        val subObject = assetsObject.optJSONObject(code)
        if (subObject == null) {
            EFLog.e("代码 '$code' 对应的子对象不存在")
            return "Unable_to_retrieve_text"
        }

        val deeperSubObject = subObject.optJSONObject(code2)
        if (deeperSubObject == null) {
            EFLog.e("代码 '$code2' 在子对象 '$code' 中对应的子对象不存在")
            return "Unable_to_retrieve_text"
        }

        val deepestSubObject = deeperSubObject.optJSONObject(code3)
        if (deepestSubObject == null) {
            EFLog.e("代码 '$code3' 在子对象 '$code2' 和 '$code' 中对应的文本为空")
            return "Unable_to_retrieve_text"
        }

        val result = deepestSubObject.optString(code4, "Unable_to_retrieve_text")
        if (result.isEmpty()) {
            EFLog.e("代码 '$code3' 在子对象 '$code2'、'$code' 和 '$code4' 中对应的文本为空")
            return "Unable_to_retrieve_text"
        }

        EFLog.d("成功获取代码 '$code3'、'$code2'、'$code' 和 '$code4' 对应的文本: $result")
        return result
    }


    fun getArrayString(code: String): String {
        val json = cachedJson
        val assetsObject = json.getJSONObject(assetsPath)
        val array = assetsObject.optJSONArray(code)
        if (array == null) {
            EFLog.e("代码 '$code' 对应的数组不存在")
            return "Unable_to_retrieve_text"
        }
        val sb = StringBuilder()
        for (i in 0 until array.length()) {
            val item = array.optString(i, "Unable_to_retrieve_text")
            sb.append("${i + 1}. $item\n") // 序号从1开始
        }
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1) // 移除最后一个换行符
        }
        EFLog.d("成功获取代码 '$code' 对应的数组并拼接为字符串")
        return sb.toString()
    }

    fun getArrayString(code: String, subCode: String): String {
        val json = cachedJson
        val assetsObject = json.getJSONObject(assetsPath)
        val subObject = assetsObject.optJSONObject(code)
        if (subObject == null) {
            EFLog.e("代码 '$code' 对应的子对象不存在")
            return "Unable_to_retrieve_text"
        }
        val array = subObject.optJSONArray(subCode)
        if (array == null) {
            EFLog.e("子代码 '$subCode' 在 '$code' 对应的子对象中不存在")
            return "Unable_to_retrieve_text"
        }
        val sb = StringBuilder()
        for (i in 0 until array.length()) {
            val item = array.optString(i, "Unable_to_retrieve_text")
            sb.append("${i + 1}. $item\n") // 序号从1开始
        }
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1) // 移除最后一个换行符
        }
        EFLog.d("成功获取代码 '$code' 和 '$subCode' 对应的数组并拼接为字符串")
        return sb.toString()
    }
}