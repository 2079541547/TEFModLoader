package eternal.future.tefmodloader.debug

import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.utility.Apk
import eternal.future.tefmodloader.utility.EFLog
import eternal.future.tefmodloader.utility.EFMod
import eternal.future.tefmodloader.utility.EFModLoader
import eternal.future.tefmodloader.utility.FileUtils
import eternal.future.tefmodloader.utility.SilkCasket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import kotlin.math.log10
import kotlin.math.pow


data class Command(val command: String, val output: String)

fun File.permissionsString(): String {
    return buildString {
        append(if (canRead()) "r" else "-")
        append(if (canWrite()) "w" else "-")
        append(if (canExecute()) "x" else "-")
    }
}

fun Long.toHumanReadableSize(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return "%.1f %s".format(this / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}

@Suppress("SimpleDateFormat")
fun Long.toDateTimeString(): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(this))
}

fun parseCommand(input: String): Command {
    val parts = input.split(" ")
    if (parts.isEmpty()) {
        return Command(command = input, output = "Error: No command provided.")
    }

    val command = parts[0].lowercase().trim()
    val args = parts.drop(1)

    val options = mutableMapOf<String, String>()
    val positionalArgs = mutableListOf<String>()

    for (arg in args) {
        if (arg.startsWith("-")) {
            val optionParts = arg.split("=")
            if (optionParts.size == 2) {
                options[optionParts[0]] = optionParts[1]
            } else {
                options[arg] = ""
            }
        } else {
            positionalArgs.add(arg)
        }
    }

    return when (command) {
            "patch" -> {
                if (positionalArgs.size < 2) {
                    return Command(command = input, output = "Error: 'patch' requires two arguments: mode and apkPath.")
                }
                try {
                    val bypass = options["-bypass"]?.toBoolean() ?: true
                    val debug = options["-debug"]?.toBoolean() ?: false
                    val overrideVersion = options["-override"]?.toBoolean() ?: false

                    Apk.patch(
                        apkPath = positionalArgs[1],
                        outPath = options["-out"] ?: "${positionalArgs[1]}.patched",
                        mode = positionalArgs[0].toInt(),
                        bypass = bypass,
                        debug = debug,
                        overrideVersion = overrideVersion
                    )
                    Command(command = input, output = "Patched APK saved to: ${options["-out"] ?: "${positionalArgs[1]}.patched"}")
                } catch (e: NumberFormatException) {
                    Command(command = input, output = "Error: Invalid mode argument for 'patch'. - ${e.message}")
                } catch (e: Exception) {
                    Command(command = input, output = "Error: Failed to patch APK - ${e.message}")
                }
            }

            "sign" -> {
                if (positionalArgs.size < 2) {
                    return Command(command = input, output = "Error: 'sign' requires two arguments: inputApk and outputApk.")
                }
                EFLog.i("input: ${positionalArgs[0]}, outPut: ${positionalArgs[1]}")
                try {
                    Apk.signApk(positionalArgs[0], positionalArgs[1])
                    Command(command = input, output = "out: ${positionalArgs[1]}")
                } catch (e: Exception) {
                    Command(command = input, output = "Error: Failed to sign APK - ${e.message}")
                }
            }

            "install" -> {
                if (positionalArgs.isEmpty()) {
                    return Command(command = input, output = "Error: 'install' requires at least one argument: path.")
                }

                val installType = options["-type"] ?: "mod"
                val installPath = File(positionalArgs[0])

                return when (installType.lowercase()) {
                    "mod" -> {
                        try {
                            val targetDir = options["-target"] ?: File(State.EFModPath, UUID.randomUUID().toString()).path
                            EFMod.install(installPath.absolutePath, targetDir)
                            Command(command = input, output = "MOD installed to: $targetDir")
                        } catch (e: Exception) {
                            Command(command = input, output = "Error: Failed to install MOD - ${e.message}")
                        }
                    }

                    "loader" -> {
                        try {
                            val targetDir = options["-target"] ?: File(State.EFModLoaderPath, UUID.randomUUID().toString()).path
                            EFModLoader.install(installPath.absolutePath, targetDir)
                            Command(command = input, output = "MOD Loader installed to: $targetDir")
                        } catch (e: Exception) {
                            Command(command = input, output = "Error: Failed to install MOD Loader - ${e.message}")
                        }
                    }

                    else -> Command(command = input, output = "Error: Unknown install type '$installType'. Supported types are 'mod' and 'loader'.")
                }
            }

        "package" -> {
            if (positionalArgs.size < 3) {
                return Command(command = input, output = "Error: 'package' requires three arguments: tempDir, sourcePath and targetPath.")
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val mode = options["-mode"] ?: "release"
                    val result = when (mode.lowercase()) {
                        "release" -> {
                            SilkCasket.release(positionalArgs[0], positionalArgs[1], positionalArgs[2])
                            "Released package saved to: ${positionalArgs[2]}"
                        }
                        "compress" -> {
                            SilkCasket.compress(positionalArgs[0], positionalArgs[1], positionalArgs[2])
                            "Compressed package saved to: ${positionalArgs[2]}"
                        }
                        else -> "Error: Unknown package mode '$mode'. Supported modes are 'release' and 'compress'."
                    }

                    withContext(Dispatchers.Main) {
                        Command(command = input, output = result)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Command(command = input, output = "Error: Failed to package - ${e.message}")
                    }
                }
            }

            Command(command = input, output = "[${options["-mode"] ?: "release"}] Packaging operation started...")
        }

        "cp" -> {
            if (positionalArgs.size < 2) {
                return Command(command = input, output = "Error: 'cp' requires two arguments: source and destination.")
            }
            try {
                val source = File(positionalArgs[0])
                val dest = File(positionalArgs[1])
                val recursive = options["-r"]?.toBoolean() ?: false
                val force = options["-f"]?.toBoolean() ?: false

                if (recursive) {
                    FileUtils.copyRecursivelyEfficient(source, dest)
                } else {
                    if (source.isDirectory) {
                        return Command(command = input, output = "Error: Source is a directory. Use '-r' for recursive copy.")
                    }
                    if (dest.exists() && !force) {
                        return Command(command = input, output = "Error: Destination file exists. Use '-f' to overwrite.")
                    }
                    source.copyTo(dest, overwrite = force)
                }
                Command(command = input, output = "Copied: ${source.path} to ${dest.path}")
            } catch (e: Exception) {
                Command(command = input, output = "Error: Failed to copy - ${e.message}")
            }
        }

        "mv" -> {
            if (positionalArgs.size < 2) {
                return Command(command = input, output = "Error: 'mv' requires two arguments: source and destination.")
            }
            try {
                val source = File(positionalArgs[0])
                val dest = File(positionalArgs[1])
                val force = options["-f"]?.toBoolean() ?: false

                if (dest.exists() && !force) {
                    return Command(command = input, output = "Error: Destination exists. Use '-f' to overwrite.")
                }

                FileUtils.moveRecursivelyEfficient(source, dest)
                Command(command = input, output = "Moved: ${source.path} to ${dest.path}")
            } catch (e: Exception) {
                Command(command = input, output = "Error: Failed to move - ${e.message}")
            }
        }

        "rm" -> {
            if (positionalArgs.isEmpty()) {
                return Command(command = input, output = "Error: 'rm' requires at least one argument: path.")
            }
            try {
                val recursive = options["-r"]?.toBoolean() ?: false
                val force = options["-f"]?.toBoolean() ?: false

                positionalArgs.forEach { path ->
                    val file = File(path)
                    if (file.isDirectory && !recursive) {
                        return Command(command = input, output = "Error: '${file.path}' is a directory. Use '-r' for recursive delete.")
                    }
                    if (!file.exists() && !force) {
                        return Command(command = input, output = "Error: '${file.path}' does not exist.")
                    }

                    if (file.isDirectory) {
                        FileUtils.deleteDirectory(file)
                    } else {
                        if (!file.delete() && !force) {
                            throw IOException("Failed to delete file: ${file.path}")
                        }
                    }
                }
                Command(command = input, output = "Deleted: ${positionalArgs.joinToString(", ")}")
            } catch (e: Exception) {
                Command(command = input, output = "Error: Failed to delete - ${e.message}")
            }
        }

        "ls" -> {
            try {
                val path = if (positionalArgs.isNotEmpty()) positionalArgs[0] else "."
                val showHidden = options["-a"]?.toBoolean() ?: false
                val longFormat = options["-l"]?.toBoolean() ?: false

                val dir = File(path)
                if (!dir.exists() || !dir.isDirectory) {
                    return Command(command = input, output = "Error: '$path' is not a valid directory.")
                }

                val files = dir.listFiles()?.filter { showHidden || !it.isHidden } ?: emptyList()

                val output = if (longFormat) {
                    files.joinToString("\n") { file ->
                        "${file.permissionsString()} ${file.length().toHumanReadableSize()} ${file.lastModified().toDateTimeString()} ${file.name}"
                    }
                } else {
                    files.joinToString("  ") { it.name }
                }

                Command(command = input, output = output)
            } catch (e: Exception) {
                Command(command = input, output = "Error: Failed to list directory - ${e.message}")
            }
        }

        "mkdir" -> {
            if (positionalArgs.isEmpty()) {
                return Command(command = input, output = "Error: 'mkdir' requires at least one argument: path.")
            }
            try {
                val parents = options["-p"]?.toBoolean() ?: false
                val results = mutableListOf<String>()

                positionalArgs.forEach { path ->
                    val dir = File(path)
                    if (parents) {
                        if (dir.mkdirs()) {
                            results.add("Created directory (with parents): ${dir.path}")
                        } else {
                            throw IOException("Failed to create directory: ${dir.path}")
                        }
                    } else {
                        if (dir.mkdir()) {
                            results.add("Created directory: ${dir.path}")
                        } else {
                            throw IOException("Failed to create directory: ${dir.path}")
                        }
                    }
                }
                Command(command = input, output = results.joinToString("\n"))
            } catch (e: Exception) {
                Command(command = input, output = "Error: Failed to create directory - ${e.message}")
            }
        }

        "cat" -> {
            if (positionalArgs.isEmpty()) {
                return Command(command = input, output = "Error: 'cat' requires at least one argument: file.")
            }
            try {
                val content = positionalArgs.joinToString("\n") { path ->
                    File(path).readText()
                }
                Command(command = input, output = content)
            } catch (e: Exception) {
                Command(command = input, output = "Error: Failed to read file - ${e.message}")
            }
        }

        // 更新help命令
        "help" -> {
            Command(command = input, output = """
                Available commands:
                File Operations:
                - cp <source> <dest> [-r] [-f]       Copy files/directories
                - mv <source> <dest> [-f]            Move/rename files/directories
                - rm <path...> [-r] [-f]             Remove files/directories
                - ls [path] [-a] [-l]                List directory contents
                - mkdir <path...> [-p]               Create directories
                - cat <file...>                      Display file contents
                
                APK Operations:
                - patch <mode> <apkPath> [-out outputPath] [-bypass true/false] [-debug true/false] [-override true/false]
                - sign <inputApk> <outputApk> [-keystore path] [-alias name] [-storepass pass] [-keypass pass]
                - install <path> [-type mod/loader] [-target dir]
                - package <tempDir> <sourcePath> <targetPath> [-mode release/compress]
                - help
            """.trimIndent())
        }

            else -> Command(command = input, output = "Unknown command: $input. Type 'help' for available commands.")
        }
}