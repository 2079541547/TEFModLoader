package silkways.terraria.efmodloader.logic.mod

import android.content.Context
import android.os.Build
import android.util.Log
import eternal.future.effsystem.fileSystem
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import java.io.File
import java.io.IOException

object ModManager {

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