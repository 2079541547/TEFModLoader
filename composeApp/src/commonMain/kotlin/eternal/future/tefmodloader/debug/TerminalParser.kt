package eternal.future.tefmodloader.debug

import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.utility.Apk
import eternal.future.tefmodloader.utility.EFLog
import eternal.future.tefmodloader.utility.EFMod
import eternal.future.tefmodloader.utility.EFModLoader
import java.io.File
import java.util.UUID


data class Command(val command: String, val output: String)

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
                Apk.patchGame(mode = positionalArgs[0].toInt(), apkPath = File(positionalArgs[1]))
                Command(command = input, output = "out: ${positionalArgs[1]}")
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
                        EFMod.install(installPath.absolutePath, File(State.EFModPath, UUID.randomUUID().toString()).path)
                        Command(command = input, output = "installed: ${installPath.absolutePath}")
                    } catch (e: Exception) {
                        Command(command = input, output = "Error: Failed to install MOD - ${e.message}")
                    }
                }

                "loader" -> {
                    try {
                        EFModLoader.install(installPath.absolutePath, File(State.EFModLoaderPath, UUID.randomUUID().toString()).path)
                        Command(command = input, output = "installed: ${installPath.absolutePath}")
                    } catch (e: Exception) {
                        Command(command = input, output = "Error: Failed to install MOD Loader - ${e.message}")
                    }
                }

                else -> Command(command = input, output = "Error: Unknown install type '$installType'. Supported types are 'mod' and 'loader'.")
            }
        }

        else -> Command(command = input, output = "Unknown command: $input")
    }
}