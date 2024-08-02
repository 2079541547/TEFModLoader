package silkways.terraria.toolbox.logic.mod

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.nio.charset.StandardCharsets
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

    fun synchronizeLibnames(modDataFilename: String, modInfoFilename: String) {
        val modData = readJsonArray(modDataFilename)
        val modInfo = readJsonArray(modInfoFilename)

        // 创建一个映射，将 author 和 modName 映射到 modInfo
        val modInfoMap = mutableMapOf<String, JSONObject>()
        for (i in 0 until modInfo.length()) {
            val info = modInfo.getJSONObject(i)
            val key = "${info.getString("author")}:${info.getString("modName")}"
            modInfoMap[key] = info
        }

        // 筛选 modData 中的有效项，并同步 libname 和 enableLibName
        val updatedModData = JSONArray()
        for (i in 0 until modData.length()) {
            val item = modData.getJSONArray(i)
            if (item.length() > 0) {
                val meta = item.getJSONObject(0)
                val key = "${meta.getString("author")}:${meta.getString("modName")}"

                // 检查是否存在于 modInfoMap 中
                if (modInfoMap.containsKey(key)) {
                    val modInfoItem = modInfoMap[key]!!

                    // 同步 libname 和 enableLibName
                    val libname = meta.getString("libname")
                    val enableLibName = modInfoItem.getString("enableLibName")

                    if (libname != enableLibName) {
                        meta.put("libname", enableLibName)
                    }

                    // 添加到更新后的 modData
                    updatedModData.put(item)
                }
            }
        }

        // 写入更新后的数据
        writeJsonArray(modDataFilename, updatedModData)

        //println("更新后的 Mod Data:\n${JSON.toJSONString(updatedModData, true)}")
    }

    fun normalizeLibnames(modDataFilename: String, modInfoFilename: String) {
        val modData = readJsonArray(modDataFilename)
        val modInfo = readJsonArray(modInfoFilename)

        // 筛选出最新版本的 modInfo
        val filteredModInfo = JSONArray()
        val modInfoGroupedByAuthorModName = mutableMapOf<String, MutableList<JSONObject>>()

        for (i in 0 until modInfo.length()) {
            val info = modInfo.getJSONObject(i)
            val key = "${info.getString("author")}:${info.getString("modName")}"
            modInfoGroupedByAuthorModName.getOrPut(key) { mutableListOf() }.add(info)
        }

        for ((_, infos) in modInfoGroupedByAuthorModName) {
            val maxBuildInfo = infos.maxByOrNull { it.getInt("build") }
            maxBuildInfo?.let { filteredModInfo.put(it) }
        }

        // 创建一个映射，将 author 和 modName 映射到 modInfo
        val modInfoMap = mutableMapOf<String, JSONObject>()
        for (i in 0 until filteredModInfo.length()) {
            val info = filteredModInfo.getJSONObject(i)
            val key = "${info.getString("author")}:${info.getString("modName")}"
            modInfoMap[key] = info
        }

        // 筛选 modData 中的有效项
        val filteredModData = JSONArray()
        for (i in 0 until modData.length()) {
            val item = modData.getJSONArray(i)
            if (item.length() > 0) {
                val meta = item.getJSONObject(0)
                val key = "${meta.getString("author")}:${meta.getString("modName")}"
                val build = meta.optInt("build", -1) // 假设默认 build 值为 -1，如果不存在该键

                // 检查是否存在于 modInfoMap 中，并且 build 值匹配
                if (modInfoMap.containsKey(key) && modInfoMap[key]?.getInt("build") == build) {
                    // 检查是否有重复的 key（author:modName），如果是，则不添加
                    var isDuplicate = false
                    for (j in 0 until filteredModData.length()) {
                        val existingItem = filteredModData.getJSONArray(j)
                        if (existingItem.length() > 0) {
                            val existingMeta = existingItem.getJSONObject(0)
                            if (existingMeta.getString("author") == meta.getString("author") &&
                                existingMeta.getString("modName") == meta.getString("modName")) {
                                isDuplicate = true
                                break
                            }
                        }
                    }

                    if (!isDuplicate) {
                        filteredModData.put(item)
                    }
                }
            }
        }

        // 重新处理 modInfo 中相同 build 值的情况
        val finalModInfo = JSONArray()
        val finalModInfoGroupedByAuthorModName = mutableMapOf<String, MutableList<JSONObject>>()

        for (i in 0 until filteredModInfo.length()) {
            val info = filteredModInfo.getJSONObject(i)
            val key = "${info.getString("author")}:${info.getString("modName")}"
            finalModInfoGroupedByAuthorModName.getOrPut(key) { mutableListOf() }.add(info)
        }

        for ((_, infos) in finalModInfoGroupedByAuthorModName) {
            val firstInfo = infos.first()
            finalModInfo.put(firstInfo)
        }

        // 写入过滤后的数据
        writeJsonArray(modDataFilename, filteredModData)
        writeJsonArray(modInfoFilename, finalModInfo)
    }


    fun removeMatchedEntries(modDataFilename: String, modInfoFilename: String, modName: String, author: String): Array<String> {
        val modData = readJsonArray(modDataFilename)
        val modInfo = readJsonArray(modInfoFilename)

        // 创建一个映射，将 author 和 modName 组合作为键，对应的 modInfo 作为值
        val modInfoMap = mutableMapOf<String, JSONObject>()
        for (i in 0 until modInfo.length()) {
            val info = modInfo.getJSONObject(i)
            val infoAuthor = info.getString("author")
            val infoModName = info.getString("modName")
            modInfoMap["$infoAuthor:$infoModName"] = info
        }

        // 筛选 modData 中的有效项
        val filteredModData = JSONArray()
        var removedLibname: String? = null
        var removedAuthorModName: String? = null
        for (i in 0 until modData.length()) {
            val item = modData.getJSONArray(i)
            if (item.length() > 0) {
                val meta = item.getJSONObject(0)
                val metaAuthor = meta.getString("author")
                val metaModName = meta.getString("modName")
                if (metaAuthor != author || metaModName != modName) {
                    filteredModData.put(item)
                } else {
                    removedLibname = meta.getString("libname")
                    removedAuthorModName = "$metaModName-$metaAuthor"
                }
            }
        }

        // 移除 modInfo 中匹配的条目
        val filteredModInfo = JSONArray()
        for (i in 0 until modInfo.length()) {
            val info = modInfo.getJSONObject(i)
            val infoAuthor = info.getString("author")
            val infoModName = info.getString("modName")
            if (infoAuthor != author || infoModName != modName) {
                filteredModInfo.put(info)
            }
        }

        // 写入过滤后的数据
        writeJsonArray(modDataFilename, filteredModData)
        writeJsonArray(modInfoFilename, filteredModInfo)

        println("修改后的mod_info: \n" + filteredModInfo.toString(4) + "\n修改后的mod_data: \n" + filteredModData.toString(4))

        // 返回被删除的 libname 和 modName-author 的组合
        return arrayOf(removedLibname.orEmpty(), removedAuthorModName.orEmpty())
    }

    fun updateEnable(modDataFilename: String, modName: String, author: String, newValue: Boolean) {
        val modData = readJsonArray(modDataFilename)

        // 筛选 modData 中的有效项
        val filteredModData = JSONArray()
        for (i in 0 until modData.length()) {
            val item = modData.getJSONArray(i)
            if (item.length() > 0) {
                val meta = item.getJSONObject(0)
                val metaAuthor = meta.getString("author")
                val metaModName = meta.getString("modName")
                if (metaAuthor == author && metaModName == modName) {
                    // 修改 enable 的值
                    meta.put("enable", newValue)
                }
                filteredModData.put(item)
            }
        }

        // 写入过滤后的数据
        writeJsonArray(modDataFilename, filteredModData)
    }

    fun filterAndGroupModData(modsFilePath: String, modsInfoFilePath: String) {
        val mods = readJsonArray(modsFilePath)
        val modsInfo = readJsonArray(modsInfoFilePath)

        // 重命名 libname 并更新 modsInfo 中的 enableLibName
        val libnameMap = mutableMapOf<String, Int>()

        // 遍历 mods 数组中的每个顶级数组
        for (i in 0 until mods.length()) {
            val modArray = mods.getJSONArray(i)
            val modObj = modArray.getJSONObject(0)  // 提取第一个元素，即 JSONObject
            val libname = modObj.getString("libname").removeSuffix(".so")
            val count = libnameMap.getOrDefault(libname, 0) + 1
            val newLibname = "$libname$count.so"
            modObj.put("libname", newLibname)
            libnameMap[libname] = count
        }

        // 遍历 modsInfo 数组中的每个 JSONObject
        for (i in 0 until modsInfo.length()) {
            val infoObj = modsInfo.getJSONObject(i)
            val author = infoObj.getString("author")
            val modName = infoObj.getString("modName")

            // 查找 mods 中对应的 modObj
            for (j in 0 until mods.length()) {
                val modArray = mods.getJSONArray(j)
                val modObj = modArray.getJSONObject(0)  // 再次提取第一个元素，即 JSONObject
                if (modObj.getString("author") == author &&
                    modObj.getString("modName") == modName
                ) {
                    val newLibname = modObj.getString("libname")
                    infoObj.put("enableLibName", newLibname)
                    break
                }
            }
        }

        // 将过滤后的数据写回文件
        writeJsonArray(modsFilePath, mods)
        writeJsonArray(modsInfoFilePath, modsInfo)
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
                val jsonFilePath2 = File(folderInRoot, "mod_info.json").absolutePath

                // 合并 JSON 文件
                if (File(jsonFilePath1).exists()) {
                    mergeJsonFiles("$extractToPath/mod_data.json", jsonFilePath1)
                    mergedJsonPaths["mod_data.json"] = jsonFilePath1
                }
                if (File(jsonFilePath2).exists()) {
                    mergeJsonFiles("$extractToPath/mod_info.json", jsonFilePath2)
                    mergedJsonPaths["mod_info.json"] = jsonFilePath2
                }

                File(jsonFilePath1).delete()
                File(jsonFilePath2).delete()
            }
        } else {
            println("没有找到根目录下的文件夹。")
        }
    }

}