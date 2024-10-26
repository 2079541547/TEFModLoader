package silkways.terraria.efmodloader.logic

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
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


    fun createJsonConfig(context: Context, fileName: String, data: Map<String, Any>) {

        val externalStorageDirectory = context.getExternalFilesDir(null)
        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            try {
                filePath.createNewFile()
            } catch (e: Exception) {
                println("无法创建文件: ${e.message}")
                return
            }

            val jsonObject = JSONObject()

            for ((key, value) in data) {
                when (value) {
                    is String -> jsonObject.put(key, value)
                    is Int -> jsonObject.put(key, value)
                    is Boolean -> jsonObject.put(key, value)
                    is List<*> -> jsonObject.put(key, JSONArray(value))
                    else -> throw IllegalArgumentException("密钥类型不受支持: '$key'")
                }
            }

            val jsonString = jsonObject.toString(4)

            try {
                val writer = FileWriter(filePath)
                writer.write(jsonString)
                writer.close()
                println("创建成功: $filePath")
            } catch (e: Exception) {
                println("无法写入文件: ${e.message}")
            }
        } else {
            println("文件已存在: $filePath")
        }
    }

    //修改json配置
    fun modifyJsonConfig(context: Context, fileName: String, key: String, newValue: Any) {

        val externalStorageDirectory = context.getExternalFilesDir(null)

        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            println("文件不存在: $filePath")
            return
        }

        val jsonFileReader = FileReader(filePath)
        val jsonObject = JSONObject(jsonFileReader.readText())

        when (newValue) {
            is String -> jsonObject.put(key, newValue)
            is Int -> jsonObject.put(key, newValue)
            is Boolean -> jsonObject.put(key, newValue)
            is List<*> -> jsonObject.put(key, JSONArray(newValue))
            else -> throw IllegalArgumentException("密钥类型不受支持: '$key'")
        }

        val jsonString = jsonObject.toString(4)

        val writer = FileWriter(filePath)
        writer.write(jsonString)
        writer.close()

        println("JSON配置文件更新于: $filePath")
    }

    //读取值
    @JvmStatic
    fun readJsonValue(context: Context, fileName: String, key: String): Any? {

        val externalStorageDirectory = context.getExternalFilesDir(null)

        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            println("文件不存在: $filePath")
            return null
        }

        val jsonObject = JSONObject(FileReader(filePath).readText())

        return when (val value = jsonObject.get(key)) {
            is String -> value
            is Int -> value
            is Boolean -> value
            is JSONArray -> value.toList()
            else -> value
        }
    }

    private fun JSONArray.toList(): List<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until length()) {
            list.add(get(i))
        }
        return list
    }


    fun modifyJsonValueInArray(context: Context, fileName: String, key: String, index: Int, newValue: Any) {

        val externalStorageDirectory = context.getExternalFilesDir(null)

        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            println("文件不存在: $filePath")
            return
        }

        val jsonObject = JSONObject(FileReader(filePath).readText())

        val jsonArray = jsonObject.getJSONArray(key)

        if (index < 0 || index >= jsonArray.length()) {
            println("数组“$key”的索引 $index 超出界限。")
            return
        }

        when (newValue) {
            is String -> jsonArray.put(index, newValue)
            is Int -> jsonArray.put(index, newValue)
            is Boolean -> jsonArray.put(index, newValue)
            else -> throw IllegalArgumentException("密钥类型不受支持: '$key'")
        }

        jsonObject.put(key, jsonArray)

        val jsonString = jsonObject.toString(4)

        val writer = FileWriter(filePath)
        writer.write(jsonString)
        writer.close()

        println("JSON配置文件更新: $filePath")
    }


    fun addNewKeyToJson(context: Context, fileName: String, newKey: String, value: Any) {

        val externalStorageDirectory = context.getExternalFilesDir(null)

        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            println("文件不存在于 $filePath")
            return
        }

        val jsonObject = JSONObject(FileReader(filePath).readText())

        if (jsonObject.has(newKey)) {
            println("键 '$newKey' 已经存在于 JSON 配置文件中，跳过添加。")
            return
        }

        when (value) {
            is String -> jsonObject.put(newKey, value)
            is Int -> jsonObject.put(newKey, value)
            is Boolean -> jsonObject.put(newKey, value)
            is List<*> -> jsonObject.put(newKey, JSONArray(value))
            else -> throw IllegalArgumentException("不支持的类型为键 '$newKey'")
        }

        val jsonString = jsonObject.toString(4)

        val writer = FileWriter(filePath)
        writer.write(jsonString)
        writer.close()

        println("新键 '$newKey' 已添加至 JSON 配置文件 $filePath")
    }

    fun removeKeyFromJson(context: Context, fileName: String, key: String) {

        val externalStorageDirectory = context.getExternalFilesDir(null)

        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            println("文件不存在于 $filePath")
            return
        }

        val jsonObject = JSONObject(FileReader(filePath).readText())

        if (!jsonObject.has(key)) {
            println("键 '$key' 不存在于 JSON 配置文件中，跳过删除。")
            return
        }

        jsonObject.remove(key)

        val jsonString = jsonObject.toString(4)

        val writer = FileWriter(filePath)
        writer.write(jsonString)
        writer.close()

        println("键 '$key' 已从 JSON 配置文件 $filePath 删除")
    }

    fun updateJsonKeys(context: Context, fileName: String, keys: Map<String, Any>) {

        val externalStorageDirectory = context.getExternalFilesDir(null)
        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            println("文件不存在于 $filePath")
            return
        }

        var jsonObject: JSONObject
        try {
            jsonObject = JSONObject(FileReader(filePath).readText())
        } catch (e: Exception) {
            println("解析 JSON 失败: ${e.message}")
            return
        }

        // 补全缺失的键
        for ((key, defaultValue) in keys) {
            if (!jsonObject.has(key)) {
                when (defaultValue) {
                    is String -> jsonObject.put(key, defaultValue)
                    is Int -> jsonObject.put(key, defaultValue)
                    is Boolean -> jsonObject.put(key, defaultValue)
                    is List<*> -> jsonObject.put(key, JSONArray(defaultValue))
                    else -> throw IllegalArgumentException("不支持的类型为键 '$key'")
                }
            }
        }

        // 删除多余的键
        jsonObject.keys().forEach {
            if (!keys.containsKey(it)) {
                jsonObject.remove(it)
            }
        }

        val jsonString = jsonObject.toString(4)

        try {
            val writer = FileWriter(filePath)
            writer.write(jsonString)
            writer.close()
            println("JSON 配置文件已更新: $filePath")
        } catch (e: IOException) {
            println("无法写入文件: ${e.message}")
        }
    }

}