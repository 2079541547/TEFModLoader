package silkways.terraria.efmodloader.utility

import silkways.terraria.efmodloader.State
import silkways.terraria.efmodloader.data.EFMod
import java.io.File

object EFMod {

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

    fun loadModsFromDirectory(targetDirectory: String): List<EFMod>  { return emptyList() }

}