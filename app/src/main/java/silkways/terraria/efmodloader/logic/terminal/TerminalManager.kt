/*******************************************************************************
 * 文件名称: TerminalManager
 * 项目名称: TEFModLoader-Compose
 * 创建时间: 2024/11/17 上午8:25
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 描述信息: 本文件为TEFModLoader-Compose项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/


package silkways.terraria.efmodloader.logic.terminal

import android.app.Activity
import android.content.Context
import eternal.future.effsystem.Tool
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.efmod.LoaderManager
import silkways.terraria.efmodloader.logic.efmod.ModManager.install
import silkways.terraria.efmodloader.ui.activity.TerminalViewModel
import java.io.File
import java.util.Locale

data class CommandResult(val output: String)

class TerminalManager(context: Context) {

    var workPath = context.dataDir.toString()
    private val commandMap = mutableMapOf<String, (List<String>) -> CommandResult>()

    init {

        registerCommand("ls") { args ->
            if (args.isNotEmpty()) {
                CommandResult("error: 'ls' does not accept arguments")
            } else {
                CommandResult(ls(workPath))
            }
        }

        registerCommand("cd") { args ->
            if (args.isNotEmpty()) {
                if(!File(args.first()).exists()) {
                    CommandResult("error: The specified path does not exist")
                } else if(!File(args.first()).isDirectory) {
                    CommandResult("error: The specified path is not a directory")
                } else {
                    workPath = args.first()
                    CommandResult("Changed to directory: ${args.first()}")
                }
            } else {
                CommandResult("error: No directory specified")
            }
        }

        registerCommand("pwd") { _ ->
            CommandResult("Current working directory: ${context.dataDir}")
        }

        registerCommand("whoami") { _ ->
            CommandResult("Current user: ${context.packageName}")
        }

        registerCommand("clear") { _ ->
            TerminalViewModel().clearHistory()
            CommandResult("")
        }

        registerCommand("exit") { _ ->
            val a = context as Activity
            a.finishAffinity()
            CommandResult("Exiting...")
        }

        registerCommand("help") { args ->
            CommandResult("""
            Available commands:
            ls - List files in the current directory
            cd [path] - Change the current directory
            pwd - Print the current working directory
            whoami - Print the current user
            clear - Clear the terminal screen
            exit - Exit the application
            help - Display this help message
            install -mod [file] - Install a mod from the specified file
            install-loader [file] - Install a loader from the specified file
            EFModTool -c -mod [name] [version] [description] - Create a new mod
            EFModTool -c loader [name] [version] [description] - Create a new loader
            EFModTool -b -mod [version] [name] [output] - Build a mod
            EFModTool -b loader [version] [name] [output] - Build a loader
            """.trimIndent())
        }

        registerCommand("install") { args ->
            if (args.isNotEmpty()) {
                when(args[0]) {
                    "-mod" -> {
                        if (!File(args[1]).exists()) {
                            CommandResult("'${args[1]}' does not exist")
                        } else if (File(args[1]).isDirectory) {
                            CommandResult("'${args[1]}' is a directory, not a file")
                        } else {
                            install(context, File(args[1]), File("${context.getExternalFilesDir(null)?.absolutePath}/TEFModLoader/EFModData"))
                            CommandResult("Mod installed successfully")
                        }
                    }
                    "-loader" -> {
                        if (!File(args[1]).exists()) {
                            CommandResult("'${args[1]}' does not exist")
                        } else if (File(args[1]).isDirectory) {
                            CommandResult("'${args[1]}' is a directory, not a file")
                        } else {
                            LoaderManager.install(context, File(args[1]), File("${context.getExternalFilesDir(null)?.absolutePath}/TEFModLoader/EFModLoaderData"))
                            CommandResult("Loader installed successfully")
                        }
                    }
                    else -> CommandResult("error: Invalid usage of 'install'")
                }
            } else {
                CommandResult("error: No arguments provided for 'install'")
            }
        }

        registerCommand("EFModTool") { args ->
            if (args.isEmpty()) {
                CommandResult("error: No arguments provided for 'EFModTool'")
            } else {
                when (args[0]) {
                    "-c" -> {
                        when (args.getOrNull(1)) {
                            "-mod" -> {
                                if (args.size >= 5) {
                                    Tool.createMod(args[2], args[3], args[4])
                                    CommandResult("Mod created successfully")
                                } else {
                                    CommandResult("error: Invalid usage of 'EFModTool -c -mod'")
                                }
                            }
                            "loader" -> {
                                if (args.size >= 5) {
                                    Tool.createLoader(args[2], args[3], args[4])
                                    CommandResult("Loader created successfully")
                                } else {
                                    CommandResult("error: Invalid usage of 'EFModTool -c loader'")
                                }
                            }
                            else -> CommandResult("error: Invalid usage of 'EFModTool -c'")
                        }
                    }
                    "-b" -> {
                        when (args.getOrNull(1)) {
                            "-mod" -> {
                                if (args.size >= 5) {
                                    try {
                                        Tool.buildMod(args[2].toInt(), args[3], args[4])
                                        CommandResult("Mod built successfully")
                                    } catch (e: NumberFormatException) {
                                        CommandResult("error: Invalid version number")
                                    }
                                } else {
                                    CommandResult("error: Invalid usage of 'EFModTool -b -mod'")
                                }
                            }
                            "loader" -> {
                                if (args.size >= 5) {
                                    try {
                                        Tool.buildLoader(args[2].toInt(), args[3], args[4])
                                        CommandResult("Loader built successfully")
                                    } catch (e: NumberFormatException) {
                                        CommandResult("error: Invalid version number")
                                    }
                                } else {
                                    CommandResult("error: Invalid usage of 'EFModTool -b loader'")
                                }
                            }
                            else -> CommandResult("error: Invalid usage of 'EFModTool -b'")
                        }
                    }
                    else -> CommandResult("error: Invalid usage of 'EFModTool'")
                }
            }
        }
    }

    fun execute(command: String): CommandResult {
        val parts = command.trim().split("\\s+".toRegex())
        val cmdName = parts.firstOrNull()?.toLowerCase(Locale.ROOT)
        val arguments = parts.drop(1)
        return commandMap[cmdName]?.invoke(arguments) ?: CommandResult("")
    }

    fun registerCommand(name: String, action: (List<String>) -> CommandResult) {
        commandMap[name.toLowerCase(Locale.ROOT)] = action
    }

    private fun ls(directoryPath: String): String {
        val directory = File(directoryPath)

        return if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()

            if (files != null) {
                EFLog.d(files.joinToString("\n"))
            }

            if (files == null) {
                "error: Could not read directory"
            } else if (files.isEmpty()) {
                "error: Directory is empty"
            } else {
                files.joinToString("\n") { it.name }
            }
        } else {
            "error: Path does not exist or is not a directory"
        }
    }
}