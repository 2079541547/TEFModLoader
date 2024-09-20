package silkways.terraria.efmodloader.ui.fragment.manage.mod

import android.content.Context
import org.json.JSONArray
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class JsonParser(private val context: Context) {

    fun parseJson(filePath: String): List<ModDetail> {
        val jsonString = loadJsonFromFile(filePath)
        val jsonArray = JSONArray(jsonString)
        return parseJsonArray(jsonArray)
    }

    private fun loadJsonFromFile(filePath: String): String {
        val jsonString: String
        try {
            val file = File(filePath)
            val inputStream = FileInputStream(file)
            jsonString = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return jsonString
    }

    private fun parseJsonArray(jsonArray: JSONArray): List<ModDetail> {
        val modList = mutableListOf<ModDetail>()

        // 遍历顶层数组中的每个元素
        for (i in 0 until jsonArray.length()) {
            // 获取每个顶层元素
            val topLevelElement = jsonArray.getJSONArray(i)

            // 处理第一个元素，即 mod 的基本信息
            val modInfoObject = topLevelElement.getJSONObject(0)
            val author = modInfoObject.getString("author")
            val modName = modInfoObject.getString("modName")
            val build = modInfoObject.getInt("build")
            val modIntroduce = modInfoObject.getString("modIntroduce")
            val opencode = modInfoObject.getBoolean("Opencode")
            val opencodeUrl = modInfoObject.optString("OpencodeUrl", null)
            val enable = modInfoObject.getBoolean("enable")
            val libname = modInfoObject.getString("libname")

            // 处理第二个元素，即函数钩子信息
            val functionsJsonArray = topLevelElement.getJSONArray(1)
            val functions = parseFunctions(functionsJsonArray)

            // 创建 ModDetail 实例
            val modDetail = ModDetail(author, modName, build, modIntroduce, enable, functions, libname, opencode, opencodeUrl)
            modList.add(modDetail)
        }

        return modList
    }


    private fun parseFunctions(functionsJsonArray: JSONArray): List<FunctionHook> {
        val functionHooks = mutableListOf<FunctionHook>()
        for (i in 0 until functionsJsonArray.length()) {
            val functionObject = functionsJsonArray.getJSONObject(i)
            val position = functionObject.getString("position")
            val functionsJsonArray = functionObject.getJSONArray("function")
            val functions = (0 until functionsJsonArray.length()).map { functionsJsonArray.getString(it) }
            val type = functionObject.getString("type")
            val arrays = functionObject.getInt("arrays")
            functionHooks.add(FunctionHook(position, functions, type, arrays))
        }
        return functionHooks
    }
}