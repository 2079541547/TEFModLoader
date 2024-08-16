package silkways.terraria.toolbox.logic.mod

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object ModJsonManager {

    fun readJsonArray(filename: String): JSONArray {
        val file = File(filename)
        return JSONArray(file.readText())
    }

    fun writeJsonArray(filename: String, jsonArray: JSONArray) {
        val file = File(filename)
        val writer = FileWriter(file)

        // 使用 org.json 库美化输出
        writer.write(jsonArray.toString(4)) // 4 是缩进空格的数量

        writer.close()
    }


    private fun mergeJsonFiles(jsonFilePath1: String, jsonFilePath2: String) {
        val jsonArray1 = readJsonArray(jsonFilePath1)
        val jsonArray2 = readJsonArray(jsonFilePath2)

        // 将第二个 JSON 数组的内容添加到第一个 JSON 数组中
        for (i in 0 until jsonArray2.length()) {
            jsonArray1.put(jsonArray2.get(i))
        }

        //美化输出
        writeJsonArray(jsonFilePath1, jsonArray1)
    }

    fun extractAndMergeJsonFiles(zipFilePaths: List<String>, extractToPath: String) {
        val foldersInRoot = mutableSetOf<String>()
        val mergedJsonPaths = mutableMapOf<String, String>()

        // 遍历每个ZIP文件
        for (zipFilePath in zipFilePaths) {
            val zipFile = File(zipFilePath)
            val zipInputStream = ZipInputStream(FileInputStream(zipFile))

            // 解压ZIP文件
            var zipEntry: ZipEntry?
            while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                zipEntry?.let { entry ->
                    val entryName = entry.name
                    val firstPart = entryName.split('/')[0]

                    // 检查是否是根目录下的文件夹
                    if (entryName.count { it == '/' } <= 1) {
                        foldersInRoot.add(firstPart)
                    }

                    val targetPath = File(extractToPath, entryName)
                    if (entry.isDirectory) {
                        // 如果是目录，创建它
                        targetPath.mkdirs()
                    } else {
                        // 如果是文件，解压到对应的目录
                        targetPath.parentFile?.mkdirs()
                        targetPath.writeBytes(zipInputStream.readBytes())
                    }
                }
                zipInputStream.closeEntry()
            }
            zipInputStream.close()
        }

        // 检查是否有找到根目录下的文件夹
        if (foldersInRoot.isNotEmpty()) {
            for (folderName in foldersInRoot) {
                val folderInRoot = File(extractToPath, folderName)
                val jsonFilePath1 = File(folderInRoot, "mod_data.json").absolutePath

                // 合并 JSON 文件
                if (File(jsonFilePath1).exists()) {
                    mergeJsonFiles("$extractToPath/mod_data.json", jsonFilePath1)
                    mergedJsonPaths["mod_data.json"] = jsonFilePath1
                }

                File(jsonFilePath1).delete()
            }
        } else {
            println("没有找到根目录下的文件夹。")
        }
    }


    fun JSON_adjustment(jsonPath: String) {
        val jsonArray = readJsonArray(jsonPath)
        val map = mutableMapOf<Pair<String, String>, Pair<JSONObject, JSONArray>>()

        for (i in 0 until jsonArray.length()) {
            val entry = jsonArray.getJSONArray(i)

            val mod = entry.getJSONObject(0)
            val hooks = entry.getJSONArray(1)

            val key = Pair(mod.getString("author"), mod.getString("modName"))

            if (!map.containsKey(key) || map[key]?.first?.getInt("build")!! < mod.getInt("build")) {
                map[key] = Pair(mod, hooks)
            }
        }

        val processedJsonArray = JSONArray()
        map.values.forEach { (mod, hooks) ->
            val entry = JSONArray()
            entry.put(mod)
            entry.put(hooks)
            processedJsonArray.put(entry)
        }

        //println(processedJsonArray.toString(4))
        writeJsonArray(jsonPath, processedJsonArray)
    }


    fun removeEntriesByAuthorAndModName(jsonPath: String, author: String, modName: String) {

        val jsonArray = readJsonArray(jsonPath)
        val filteredJsonArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            val entry = jsonArray.getJSONArray(i)

            val mod = entry.getJSONObject(0)
            val currentAuthor = mod.getString("author")
            val currentModName = mod.getString("modName")

            // 如果当前条目的 author 和 modName 不匹配给定的值，则保留该条目
            if (currentAuthor != author || currentModName != modName) {
                filteredJsonArray.put(entry)
            }
        }

        //println(filteredJsonArray.toString(4))
        writeJsonArray(jsonPath, filteredJsonArray)
    }


    fun updateEnableByAuthorAndModName(jsonPath: String, author: String, modName: String, newValue: Boolean) {

        val jsonArray = readJsonArray(jsonPath)
        val updatedJsonArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            val entry = jsonArray.getJSONArray(i)

            val mod = entry.getJSONObject(0)
            val currentAuthor = mod.getString("author")
            val currentModName = mod.getString("modName")

            // 如果当前条目的 author 和 modName 匹配给定的值，则更新 enable 值
            if (currentAuthor == author && currentModName == modName) {
                mod.put("enable", newValue)
            }

            updatedJsonArray.put(entry)
        }

        //println(updatedJsonArray.toString(4))
        writeJsonArray(jsonPath, updatedJsonArray)
    }



    fun modifyArrays(jsonPath: String, author: String, modName: String, position: String, newValue: Int) {


        // 将 JSON 字符串解析为 JSONArray
        val jsonArray = readJsonArray(jsonPath)

        // 获取嵌套的 JSONArray
        val modsArray = jsonArray.getJSONArray(0)

        // 遍历 modsArray 查找目标 mod
        for (i in 0 until modsArray.length()) {
            val modJson = modsArray.getJSONObject(i)

            // 检查 author 和 modName 是否匹配
            if (modJson.getString("author") == author && modJson.getString("modName") == modName) {
                // 获取嵌套的 JSONArray 对应于 modifications
                val modificationsArray = modsArray.getJSONArray(1)

                // 遍历 modifications 查找匹配的 position
                for (j in 0 until modificationsArray.length()) {
                    val modificationJson = modificationsArray.getJSONObject(j)

                    // 检查 position 是否匹配
                    if (modificationJson.getString("position") == position) {
                        // 更新 arrays 的值
                        modificationJson.put("arrays", newValue)

                        // 退出循环，因为我们只需要修改一个位置
                        break
                    }
                }

                // 退出循环，因为我们只需要修改一个 mod
                break
            }
        }

        //println(jsonArray.toString(4))
        writeJsonArray(jsonPath, jsonArray)
    }

}