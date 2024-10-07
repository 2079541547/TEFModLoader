package silkways.terraria.efmodloader.logic.mod

import android.content.Context
import android.os.Build
import android.util.Log
import eternal.future.effsystem.fileSystem
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import java.io.File
import java.io.IOException

object ModManager {

    fun enableEFMod(context: Context, filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e("EFModLoader", "文件不存在: $filePath")
            return
        }
        try {
            fileSystem.EFMC.extractExecutable(file.absolutePath, Build.CPU_ABI, context.cacheDir.absolutePath + "/runEFMod")
        } catch (e: IOException) {
            Log.e("EFModLoader", "解压过程中发生错误", e)
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