package silkways.terraria.toolbox.logic

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader


object JsonConfigModifier {


    fun getAssetsArray(context: Context, filePath: String, key: String): JSONArray? {
        // 用于读取 assets 文件夹中的 JSON 文件
        val jsonString = context.assets.open(filePath).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readText()
            }
        }

        // 解析 JSON 字符串
        val jsonObject = try {
            JSONObject(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        // 获取指定键对应的数组
        return jsonObject.optJSONArray(key)
    }
}