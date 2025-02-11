package eternal.future.efmodloader.utility

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import net.peanuuutz.tomlkt.Toml
import net.peanuuutz.tomlkt.asTomlTable
import net.peanuuutz.tomlkt.getArray
import net.peanuuutz.tomlkt.getBoolean
import net.peanuuutz.tomlkt.getInteger
import net.peanuuutz.tomlkt.getString
import net.peanuuutz.tomlkt.getTable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import eternal.future.efmodloader.State
import eternal.future.efmodloader.data.EFMod
import eternal.future.efmodloader.data.Github
import eternal.future.efmodloader.data.Info
import eternal.future.efmodloader.data.Introduction
import eternal.future.efmodloader.data.LoaderSupport
import eternal.future.efmodloader.data.PlatformSupport
import eternal.future.efmodloader.data.Platforms
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object EFMod {

    fun install(modFile: String, targetDirectory: String) {
        val expectedHeader = byteArrayOf(
            0x53, 0x69, 0x6C, 0x6B, 0x43, 0x61, 0x73, 0x6B, 0x65, 0x74,
            0x00, 0x03, 0xFE.toByte(), 0x34, 0x01
        )

        try {
            FileInputStream(modFile).use { fis ->
                val header = ByteArray(expectedHeader.size)
                val bytesRead = fis.read(header)

                if (bytesRead == expectedHeader.size || header.contentEquals(expectedHeader)) {
                    SilkCasket.release(State.SilkCasket_Temp, modFile, targetDirectory)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun update(modFile: String, targetDirectory: String) {
        val expectedHeader = byteArrayOf(
            0x53, 0x69, 0x6C, 0x6B, 0x43, 0x61, 0x73, 0x6B, 0x65, 0x74,
            0x00, 0x03, 0xFE.toByte(), 0x34, 0x01
        )

        try {
            FileInputStream(modFile).use { fis ->
                val header = ByteArray(expectedHeader.size)
                val bytesRead = fis.read(header)

                if (bytesRead == expectedHeader.size || header.contentEquals(expectedHeader)) {
                    SilkCasket.release(State.SilkCasket_Temp, modFile, targetDirectory)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun remove(targetDirectory: String) {
        FileUtils.deleteDirectory(File(targetDirectory))
    }

    fun initialize(modPath: String, loaderPath: String, targetDirectory: String) {

        data class loader(
            val name: String,
            val path: String,
            val libName: String,
            val version: List<String>,
            val supportedStandards: Pair<Int, Int>
        )

        val mods = loadModsFromDirectory(modPath)
        val loaders = EFModLoader.loadLoadersFromDirectory(loaderPath)

        val loadersMap = mutableStateListOf<loader>()
        val initializeMap = mutableMapOf<loader, MutableList<String>>()

        val architecture = when (State.architecture.value) {
            1 -> "arm64-v8a"
            2 -> "armeabi-v7a"
            3 -> "x64"
            4 -> "x86"
            else -> App.getCurrentArchitecture()
        }

        val platform = if (State.isAndroid) "android" else "windows"

        loaders.forEach {
            if (it.isEnabled) {
                val v = mutableStateListOf<String>()
                v.add(it.info.version)
                it.compatibility.supportedVersions.forEach { vv -> v.add(vv) }

                loadersMap.add(loader(
                    name = "${it.info.name}-${it.info.author}",
                    path = it.path,
                    libName = it.loader.libName,
                    version = v,
                    supportedStandards = Pair(it.compatibility.highestStandards, it.compatibility.minimumStandards)
                ))
            }
        }

        mods.forEach { mod ->
            if (mod.isEnabled) {
                if (!mod.Modx) {
                    var l = false
                    mod.loaders.forEach { loader ->
                        loadersMap.forEach { ll ->
                            println(ll)
                            if (ll.name == loader.name &&
                                ll.version.toSet().intersect(loader.supportedVersions.toSet()).isNotEmpty()) {
                                if (!l) {
                                    if (!initializeMap.containsKey(ll)) {
                                        initializeMap[ll] = mutableListOf()
                                    }
                                    initializeMap[ll]?.add(mod.path)
                                    l = true
                                }
                            } else {
                                println("No match for loader: ${loader.name} with versions: ${loader.supportedVersions}")
                            }
                        }
                    }
                } else {
                    val sourceDir = File(mod.path, "lib/$platform/$architecture")
                    val targetDir = File(targetDirectory, "Modx/${File(mod.path).name}")
                    FileUtils.copyRecursivelyEfficient(sourceDir, targetDir)
                }
            }
        }

        initializeMap.forEach { (l, m) ->
            val loaderDir = File(l.path)
            val loader = File(targetDirectory, "EFMod/${loaderDir.name}")
            val loaderLib = if (State.isAndroid) "lib${l.libName}.so" else "${l.libName}.dll"
            FileUtils.copyRecursivelyEfficient(File(loaderDir, "lib/$platform/$architecture"), loader)

            val originalFile = File(loader, loaderLib)
            val newFile = File(loader, "loader-core")
            Files.move(originalFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

            m.forEach {
                FileUtils.copyRecursivelyEfficient(File(it, "lib/$platform/$architecture"), File(targetDirectory, "EFMod/${loaderDir.name}/Mod/${File(it).name}"))
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun loadModsFromDirectory(targetDirectory: String): List<EFMod>  {
        val directory = File(targetDirectory)
        val mods = mutableListOf<EFMod>()

        if (directory.exists() && directory.isDirectory) {
            for (modDir in directory.listFiles { file -> file.isDirectory }) {
                if (modDir != null && File(modDir, "efmod.toml").exists()) {
                    var ModIcon: ImageBitmap? = null
                    val iconFile = File(modDir, "efmod.icon")
                    if (iconFile.exists()) {
                        val Icon = File(
                                modDir,
                                "efmod.icon"
                            ).readBytes()

                        if (Icon.isNotEmpty()) {
                            ModIcon = Icon.decodeToImageBitmap()
                        }
                    }

                    val toml = Toml.parseToTomlTable(File(modDir, "efmod.toml").reader().readText())

                    val loaderSupportList = mutableListOf<LoaderSupport>()

                    for (loader_suppor in toml.getArray("loaders")) {
                        loaderSupportList.add(
                            LoaderSupport(
                                name = loader_suppor.asTomlTable().getString("name"),
                                supportedVersions = loader_suppor.asTomlTable().getArray("supported_versions").map { it.toString() }
                            )
                        )
                    }

                    val mod = EFMod(
                        info = Info(
                            name = toml.getTable("info").getString("name"),
                            author = toml.getTable("info").getString("author"),
                            version = toml.getTable("info").getString("version"),
                            page = File(modDir, "Page/android").exists() && File(
                                modDir,
                                "Page/desktop"
                            ).exists()
                        ),
                        github = Github(
                            openSource = toml.getTable("github").getBoolean("open_source"),
                            overview = toml.getTable("github").getString("overview"),
                            url = toml.getTable("github").getString("url")
                        ),
                        platform = Platforms(
                            windows = PlatformSupport(
                                arm64 = toml.getTable("platform").getTable("windows")
                                    .getBoolean("arm64"),
                                arm32 = toml.getTable("platform").getTable("windows")
                                    .getBoolean("arm32"),
                                x86_64 = toml.getTable("platform").getTable("windows")
                                    .getBoolean("x86_64"),
                                x86 = toml.getTable("platform").getTable("windows")
                                    .getBoolean("x86")
                            ),
                            android = PlatformSupport(
                                arm64 = toml.getTable("platform").getTable("android")
                                    .getBoolean("arm64"),
                                arm32 = toml.getTable("platform").getTable("android")
                                    .getBoolean("arm32"),
                                x86_64 = toml.getTable("platform").getTable("android")
                                    .getBoolean("x86_64"),
                                x86 = toml.getTable("platform").getTable("android")
                                    .getBoolean("x86")
                            )
                        ),
                        loaders = loaderSupportList,
                        introduce = Introduction(
                            description = toml.getTable("introduce")
                                .getString(Locales.getLanguage(State.language.value))
                        ),
                        path = modDir.absolutePath,
                        icon = ModIcon,
                        isEnabled = File(modDir, "enabled").exists(),
                        standards = toml.getTable("info").getInteger("standards").toInt(),
                        Modx = toml.getTable("info").getBoolean("modx")
                    )

                    mods.add(mod)
                }
            }
        }
        return mods
    }
}