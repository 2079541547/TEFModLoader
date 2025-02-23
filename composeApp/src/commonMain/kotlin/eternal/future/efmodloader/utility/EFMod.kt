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
        EFLog.d("开始安装MOD文件: $modFile 到目录: $targetDirectory")
        val expectedHeader = byteArrayOf(
            0x53, 0x69, 0x6C, 0x6B, 0x43, 0x61, 0x73, 0x6B, 0x65, 0x74,
            0x00, 0x03, 0xFE.toByte(), 0x34, 0x01
        )

        try {
            FileInputStream(modFile).use { fis ->
                val header = ByteArray(expectedHeader.size)
                val bytesRead = fis.read(header)

                if (bytesRead == expectedHeader.size && header.contentEquals(expectedHeader)) {
                    EFLog.i("验证通过，开始释放MOD文件: $modFile 到目标目录: $targetDirectory")
                    SilkCasket.release(State.SilkCasket_Temp, modFile, targetDirectory)
                    EFLog.d("成功安装MOD文件: $modFile 到目录: $targetDirectory")
                } else {
                    EFLog.e("文件头验证失败: 文件 '$modFile' 不是有效的MOD文件")
                }
            }
        } catch (e: IOException) {
            EFLog.e("安装MOD文件时发生IO异常", e)
        }
    }

    fun update(modFile: String, targetDirectory: String) {
        EFLog.d("开始更新MOD文件: $modFile 到目录: $targetDirectory")
        val expectedHeader = byteArrayOf(
            0x53, 0x69, 0x6C, 0x6B, 0x43, 0x61, 0x73, 0x6B, 0x65, 0x74,
            0x00, 0x03, 0xFE.toByte(), 0x34, 0x01
        )

        try {
            FileInputStream(modFile).use { fis ->
                val header = ByteArray(expectedHeader.size)
                val bytesRead = fis.read(header)

                if (bytesRead == expectedHeader.size && header.contentEquals(expectedHeader)) {
                    EFLog.i("验证通过，开始更新MOD文件: $modFile 到目标目录: $targetDirectory")
                    SilkCasket.release(State.SilkCasket_Temp, modFile, targetDirectory)
                    EFLog.d("成功更新MOD文件: $modFile 到目录: $targetDirectory")
                } else {
                    EFLog.e("文件头验证失败: 文件 '$modFile' 不是有效的MOD文件")
                }
            }
        } catch (e: IOException) {
            EFLog.e("更新MOD文件时发生IO异常", e)
        }
    }

    fun remove(targetDirectory: String) {
        EFLog.d("开始删除目录: $targetDirectory")
        try {
            FileUtils.deleteDirectory(File(targetDirectory))
            EFLog.d("成功删除目录: $targetDirectory")
        } catch (e: IOException) {
            EFLog.e("删除目录时发生IO异常", e)
        }
    }

    fun initialize_data(modPath: String, targetDirectory: String) {
        try {
            val targetDir = File(targetDirectory)
            if (!targetDir.exists() && !targetDir.mkdirs()) {
                EFLog.e("无法创建目标目录：$targetDirectory")
                return
            }

            val mods = loadModsFromDirectory(modPath)
            mods.forEach { mod ->
                val f = File(mod.path)
                File(f, "private").let { privateDir ->
                    if (privateDir.exists()) {
                        EFLog.d("开始从 ${privateDir.absolutePath} 复制到 $targetDirectory")
                        FileUtils.copyRecursivelyEfficient(privateDir, File(targetDirectory, "${privateDir.name}/private"))
                        EFLog.d("完成复制 ${privateDir.name} 到 $targetDirectory")
                    } else {
                        EFLog.w("未找到私有目录：${privateDir.absolutePath}")
                    }
                }
            }
        } catch (e: Exception) {
            EFLog.e("初始化数据时发生异常", e)
        }
    }

    fun update_data(externallyPrivate: String, modPath: String) {
        try {
            val mods = loadModsFromDirectory(modPath)
            mods.forEach { mod ->
                val f = File(mod.path)
                File(f, "private").let { privateDir ->
                    if (privateDir.exists()) {
                        File(externallyPrivate, "${privateDir.name}/private").let { externalDir ->
                            if (externalDir.exists() && externalDir.isDirectory) {
                                if (!privateDir.exists() && !privateDir.mkdirs()) {
                                    EFLog.e("无法创建私有目录：${privateDir.absolutePath}")
                                    return@let
                                }

                                EFLog.d("开始从 ${externalDir.absolutePath} 更新到 ${privateDir.absolutePath}")
                                FileUtils.copyRecursivelyEfficient(externalDir, privateDir)
                                EFLog.d("完成更新 ${privateDir.name} 从 $externallyPrivate")
                            } else {
                                EFLog.w("外部私有目录不存在或不是目录：${externalDir.absolutePath}")
                            }
                        }
                    } else {
                        EFLog.w("未找到私有目录：${privateDir.absolutePath}")
                    }
                }
            }
        } catch (e: Exception) {
            EFLog.e("更新数据时发生异常", e)
        }
    }

    fun initialize(modPath: String, loaderPath: String, targetDirectory: String) {
        EFLog.d("开始初始化MOD和加载器: MOD路径: $modPath, 加载器路径: $loaderPath, 目标目录: $targetDirectory")

        data class Loader(
            val name: String,
            val path: String,
            val libName: String,
            val version: List<String>,
            val supportedStandards: Pair<Int, Int>
        )

        val mods = loadModsFromDirectory(modPath)
        val loaders = EFModLoader.loadLoadersFromDirectory(loaderPath)

        val loadersMap = mutableStateListOf<Loader>()
        val initializeMap = mutableMapOf<Loader, MutableList<String>>()

        val architecture = when (State.architecture.value) {
            1 -> "arm64-v8a"
            2 -> "armeabi-v7a"
            3 -> "x64"
            4 -> "x86"
            else -> App.getCurrentArchitecture()
        }

        val platform = if (State.isAndroid) "android" else "windows"

        EFLog.i("架构: $architecture, 平台: $platform")

        loaders.forEach { loaderInfo ->
            if (loaderInfo.isEnabled) {
                val versions = mutableStateListOf<String>()
                versions.add(loaderInfo.info.version)
                loaderInfo.compatibility.supportedVersions.forEach { vv -> versions.add(vv) }

                val loader = Loader(
                    name = "${loaderInfo.info.name}-${loaderInfo.info.author}",
                    path = loaderInfo.path,
                    libName = loaderInfo.loader.libName,
                    version = versions,
                    supportedStandards = Pair(loaderInfo.compatibility.highestStandards, loaderInfo.compatibility.minimumStandards)
                )
                loadersMap.add(loader)
                EFLog.v("已添加加载器: ${loader.name}, 路径: ${loader.path}")
            }
        }

        mods.forEach { mod ->
            if (mod.isEnabled) {
                if (!mod.Modx) {
                    var matched = false
                    mod.loaders.forEach { loader ->
                        loadersMap.forEach { ll ->
                            if (ll.name == loader.name &&
                                ll.version.toSet().intersect(loader.supportedVersions.toSet()).isNotEmpty() &&
                                mod.standards <= ll.supportedStandards.first &&
                                mod.standards >= ll.supportedStandards.second) {
                                if (!matched) {
                                    if (!initializeMap.containsKey(ll)) {
                                        initializeMap[ll] = mutableListOf()
                                    }
                                    initializeMap[ll]?.add(mod.path)
                                    matched = true
                                    EFLog.v("匹配到加载器: ${ll.name} 对应MOD: ${mod.path}")
                                }
                            } else {
                                EFLog.w("未找到匹配的加载器: ${loader.name} 版本: ${loader.supportedVersions} Mod标准：${mod.standards}")
                            }
                        }
                    }
                } else {
                    val sourceDir = File(mod.path, "lib/$platform/$architecture")
                    val targetDir = File(targetDirectory, "Modx/${File(mod.path).name}")
                    EFLog.v("开始复制Modx文件从: ${sourceDir.absolutePath} 到: ${targetDir.absolutePath}")
                    FileUtils.copyRecursivelyEfficient(sourceDir, targetDir)
                    EFLog.v("完成复制Modx文件从: ${sourceDir.absolutePath} 到: ${targetDir.absolutePath}")
                }
            }
        }

        initializeMap.forEach { (loader, modPaths) ->
            val loaderDir = File(loader.path)
            val loaderTargetDir = File(targetDirectory, "EFMod/${loaderDir.name}")
            val loaderLib = if (State.isAndroid) "lib${loader.libName}.so" else "${loader.libName}.dll"

            EFLog.v("开始复制加载器库文件从: ${loaderDir.absolutePath}/lib/$platform/$architecture 到: ${loaderTargetDir.absolutePath}")
            FileUtils.copyRecursivelyEfficient(File(loaderDir, "lib/$platform/$architecture"), loaderTargetDir)
            EFLog.v("完成复制加载器库文件从: ${loaderDir.absolutePath}/lib/$platform/$architecture 到: ${loaderTargetDir.absolutePath}")

            val originalFile = File(loaderTargetDir, loaderLib)
            val newFile = File(loaderTargetDir, "loader-core")
            EFLog.v("重命名加载器库文件: ${originalFile.absolutePath} 到: ${newFile.absolutePath}")
            Files.move(originalFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            EFLog.v("完成重命名加载器库文件: ${originalFile.absolutePath} 到: ${newFile.absolutePath}")

            modPaths.forEach { modPath ->
                val sourceDir = File(modPath, "lib/$platform/$architecture")
                val targetDir = File(loaderTargetDir, "Mod/${File(modPath).name}")
                EFLog.v("开始复制MOD文件从: ${sourceDir.absolutePath} 到: ${targetDir.absolutePath}")
                FileUtils.copyRecursivelyEfficient(sourceDir, targetDir)
                EFLog.v("完成复制MOD文件从: ${sourceDir.absolutePath} 到: ${targetDir.absolutePath}")
            }
        }

        EFLog.d("完成初始化MOD和加载器: MOD路径: $modPath, 加载器路径: $loaderPath, 目标目录: $targetDirectory")
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
                        standards = toml.getTable("mod").getInteger("standards").toInt(),
                        Modx = toml.getTable("mod").getBoolean("modx")
                    )

                    mods.add(mod)
                }
            }
        }
        return mods
    }
}