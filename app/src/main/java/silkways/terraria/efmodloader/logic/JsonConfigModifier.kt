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

    /**
     * 从应用程序的 assets 文件夹中读取指定 JSON 文件，并提取指定键对应的 JSON 数组。
     *
     * @param context 应用程序上下文，用于访问 assets 资源。
     * @param filePath assets 文件夹中的 JSON 文件路径。
     * @param key 需要提取的 JSON 数组的键名。
     * @return 提取的 JSON 数组，如果读取或解析失败则返回 null。
     */
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
            EFLog.e("解析 JSON 时发生错误: ${e.message}")
            return null
        }

        // 获取指定键对应的数组
        return jsonObject.optJSONArray(key)
    }

    /**
     * 创建一个新的 JSON 配置文件。
     *
     * @param context 应用程序上下文。
     * @param fileName 要创建的 JSON 文件名。
     * @param data 初始数据，键值对形式。
     */
    fun createJsonConfig(context: Context, fileName: String, data: Map<String, Any>) {
        val externalStorageDirectory = context.getExternalFilesDir(null)
        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            try {
                filePath.createNewFile()
            } catch (e: Exception) {
                EFLog.e("无法创建文件: ${e.message}")
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
                EFLog.i("创建成功: $filePath")
            } catch (e: Exception) {
                EFLog.e("无法写入文件: ${e.message}")
            }
        } else {
            EFLog.i("文件已存在: $filePath")
        }
    }

    /**
     * 修改现有的 JSON 配置文件中的某个键值。
     *
     * @param context 应用程序上下文。
     * @param fileName JSON 文件名。
     * @param key 要修改的键。
     * @param newValue 新的值。
     */
    fun modifyJsonConfig(context: Context, fileName: String, key: String, newValue: Any) {
        val externalStorageDirectory = context.getExternalFilesDir(null)
        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            EFLog.e("文件不存在: $filePath")
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

        EFLog.i("JSON配置文件更新于: $filePath")
    }

    /**
     * 从 JSON 配置文件中读取指定键的值。
     *
     * @param context 应用程序上下文。
     * @param fileName JSON 文件名。
     * @param key 要读取的键。
     * @return 读取到的值，如果文件不存在或读取失败则返回 null。
     */
    @JvmStatic
    fun readJsonValue(context: Context, fileName: String, key: String): Any? {
        val externalStorageDirectory = context.getExternalFilesDir(null)
        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            EFLog.e("文件不存在: $filePath")
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

    /**
     * 将 JSONArray 转换为 List。
     *
     * @return 转换后的列表。
     */
    private fun JSONArray.toList(): List<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until length()) {
            list.add(get(i))
        }
        return list
    }

    /**
     * 修改 JSON 配置文件中数组的某个元素。
     *
     * @param context 应用程序上下文。
     * @param fileName JSON 文件名。
     * @param key 包含数组的键。
     * @param index 要修改的数组索引。
     * @param newValue 新的值。
     */
    fun modifyJsonValueInArray(context: Context, fileName: String, key: String, index: Int, newValue: Any) {
        val externalStorageDirectory = context.getExternalFilesDir(null)
        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            EFLog.e("文件不存在: $filePath")
            return
        }

        val jsonObject = JSONObject(FileReader(filePath).readText())
        val jsonArray = jsonObject.getJSONArray(key)

        if (index < 0 || index >= jsonArray.length()) {
            EFLog.e("数组“$key”的索引 $index 超出界限。")
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

        EFLog.i("JSON配置文件更新: $filePath")
    }

    /**
     * 向 JSON 配置文件中添加新的键值对。
     *
     * @param context 应用程序上下文。
     * @param fileName JSON 文件名。
     * @param newKey 要添加的新键。
     * @param value 新键的值。
     */
    fun addNewKeyToJson(context: Context, fileName: String, newKey: String, value: Any) {
        val externalStorageDirectory = context.getExternalFilesDir(null)
        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            EFLog.e("文件不存在于 $filePath")
            return
        }

        val jsonObject = JSONObject(FileReader(filePath).readText())

        if (jsonObject.has(newKey)) {
            EFLog.i("键 '$newKey' 已经存在于 JSON 配置文件中，跳过添加。")
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

        EFLog.i("新键 '$newKey' 已添加至 JSON 配置文件 $filePath")
    }

    /**
     * 从 JSON 配置文件中删除指定的键。
     *
     * @param context 应用程序上下文。
     * @param fileName JSON 文件名。
     * @param key 要删除的键。
     */
    fun removeKeyFromJson(context: Context, fileName: String, key: String) {
        val externalStorageDirectory = context.getExternalFilesDir(null)
        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            EFLog.e("文件不存在于 $filePath")
            return
        }

        val jsonObject = JSONObject(FileReader(filePath).readText())

        if (!jsonObject.has(key)) {
            EFLog.i("键 '$key' 不存在于 JSON 配置文件中，跳过删除。")
            return
        }

        jsonObject.remove(key)

        val jsonString = jsonObject.toString(4)

        val writer = FileWriter(filePath)
        writer.write(jsonString)
        writer.close()

        EFLog.i("键 '$key' 已从 JSON 配置文件 $filePath 删除")
    }

    /**
     * 更新 JSON 配置文件中的多个键值对。
     *
     * @param context 应用程序上下文。
     * @param fileName JSON 文件名。
     * @param keys 键值对映射。
     */
    fun updateJsonKeys(context: Context, fileName: String, keys: Map<String, Any>) {
        val externalStorageDirectory = context.getExternalFilesDir(null)
        val filePath = File(externalStorageDirectory, fileName)

        if (!filePath.exists()) {
            EFLog.e("文件不存在于 $filePath")
            return
        }

        var jsonObject: JSONObject
        try {
            jsonObject = JSONObject(FileReader(filePath).readText())
        } catch (e: Exception) {
            EFLog.e("解析 JSON 失败: ${e.message}")
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
            EFLog.i("JSON 配置文件已更新: $filePath")
        } catch (e: IOException) {
            EFLog.e("无法写入文件: ${e.message}")
        }
    }
}