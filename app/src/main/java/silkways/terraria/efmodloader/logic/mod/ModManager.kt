package silkways.terraria.efmodloader.logic.mod

import android.content.Context
import android.os.Build
import android.util.Log
import eternal.future.effsystem.fileSystem
import org.json.JSONObject
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import java.io.File
import java.io.IOException

object ModManager {

    fun runEFMod(filePath: String, context: Context) {
        val file = File(filePath)
        if (file.exists()) {
            val bufferedReader = file.bufferedReader()
            val jsonString = bufferedReader.use { it.readText() }

            // 解析JSON
            val jsonObject = JSONObject(jsonString)

            // 遍历JSON对象
            val iterator = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                if (jsonObject.getBoolean(key)) {
                    if (fileSystem.EFMC.getModInfo(key)["SpecialLoading"] as Boolean) {
                        fileSystem.EFMC.extractExecutable(key, Build.CPU_ABI, "${context.cacheDir.absolutePath}/SpecialLoading/")
                    } else {
                        fileSystem.EFMC.extractExecutable(key, Build.CPU_ABI, "${context.cacheDir.absolutePath}/runEFMod/")
                    }
                    Log.d("GamePanelFragment", "Key: $key") // 如果值为true，打印键
                }
            }
        } else {
            Log.e("GamePanelFragment", "File not found at path: $filePath")
        }
    }

    fun removeEFMod(context: Context, filePath: String, identifier: String) {
        JsonConfigModifier.removeKeyFromJson(context, "ToolBoxData/EFModData/info.json", filePath)
        File(filePath).delete()

        deleteDirectory(File("${context.getExternalFilesDir(null)}/EFMod-Private/$identifier"))
    }

    private fun deleteDirectory(directory: File) {
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    file.delete()
                }
            }
            directory.delete()
        }
    }
}