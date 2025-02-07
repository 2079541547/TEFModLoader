package silkways.terraria.efmodloader.utility

import silkways.terraria.efmodloader.State
import silkways.terraria.efmodloader.data.EFModLoader
import java.io.File

object EFModLoader {

    fun install(modFile: String, targetDirectory: String) {
        SilkCasket.release(State.SilkCasket_Temp, modFile, targetDirectory)
    }

    fun update(modFile: String, targetDirectory: String) {
        SilkCasket.release(State.SilkCasket_Temp, modFile, targetDirectory)
    }

    fun remove(targetDirectory: String) {
        FileUtils.deleteDirectory(File(targetDirectory))
    }

    fun initialize(targetDirectory: String) {
        TODO()
    }

    fun loadModsFromDirectory(targetDirectory: String): List<EFModLoader>  { return emptyList() }

}