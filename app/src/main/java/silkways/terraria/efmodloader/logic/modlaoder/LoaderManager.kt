package silkways.terraria.efmodloader.logic.modlaoder

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import eternal.future.effsystem.fileSystem
import org.json.JSONObject
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import java.io.File
import silkways.terraria.efmodloader.data.TEFModLoader

object LoaderManager {

    @SuppressLint("SdCardPath")
    fun runEFModLoader(filePath: String, context: Context) {
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

                    when(JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.Runtime)) {
                        0 -> {
                            fileSystem.EFML.extractLoader(key, Build.CPU_ABI, "/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/kernel")
                        }

                        1 -> {
                            fileSystem.EFML.extractLoader(key, Build.CPU_ABI, "data/data/${JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.GamePackageName) as String}/cache/EFModLoader")
                        }
                    }

                    Log.d("GamePanelFragment", "Key: $key") // 如果值为true，打印键
                }
            }
        } else {
            Log.e("GamePanelFragment", "File not found at path: $filePath")
        }
    }


    fun removeEFModLoader(context: Context, filePath: String) {
        JsonConfigModifier.removeKeyFromJson(context, "ToolBoxData/EFModLoaderData/info.json", filePath)
        File(filePath).delete()
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