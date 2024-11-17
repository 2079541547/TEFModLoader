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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.compose.ui.text.AnnotatedString
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.ui.activity.TerminalViewModel
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File
import java.util.Locale

data class CommandResult(val output: AnnotatedString)

class TerminalManager(context: Context) {


    private val Conventional = LanguageUtils(
        MainApplication.getContext(),
        LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
        "conventional"
    )

    var workPath = context.dataDir.toString()
    private val commandMap = mutableMapOf<String, (List<String>) -> CommandResult>()


    init {

        registerCommand("ls") { args ->
            if (args.isNotEmpty()) {
                CommandResult(AnnotatedString("'${args.first()}' ${Conventional.getString("ls", "result")}${ls(args.first())}"))
            } else {
                CommandResult(AnnotatedString("'$workPath' ${Conventional.getString("ls", "result")}${ls(workPath)}"))
            }
        }

        registerCommand("cd") { args ->
            if (args.isNotEmpty()) {
                if(!File(args.first()).exists()) {
                    CommandResult(AnnotatedString(Conventional.getString("cd", "error", "1")))
                } else if(!File(args.first()).isDirectory) {
                    CommandResult(AnnotatedString(Conventional.getString("cd", "error", "2")))
                } else {
                    workPath = args.first()
                    CommandResult(AnnotatedString("${Conventional.getString("cd", "result")} ${args.first()}"))
                }
            } else {
                CommandResult(AnnotatedString(Conventional.getString("cd", "error", "0")))
            }
        }

        registerCommand("pwd") { _ ->
            CommandResult(AnnotatedString("${Conventional.getString("pwd", "result")}${context.dataDir}"))
        }

        registerCommand("whoami") { _ ->
            CommandResult(AnnotatedString("${Conventional.getString("whoami", "result")}$workPath"))
        }

        registerCommand("clear") { _ ->
            TerminalViewModel().clearHistory()
            CommandResult(AnnotatedString(""))
        }

        registerCommand("exit") { _ ->
            val a = context as Activity
            a.finishAffinity()
            CommandResult(AnnotatedString(Conventional.getString("exit", "result")))
        }

        registerCommand("help") { _ ->
            CommandResult(AnnotatedString(Conventional.getString("help", "error", "0")))
        }

        registerCommand("help-c") { args ->
            if (args.isNotEmpty()) {
                CommandResult(AnnotatedString("'${args.first()}'" + Conventional.getString("help", "result") + Conventional.getArrayString(args.first(), "document")))
            } else {
                CommandResult(AnnotatedString(Conventional.getString("help", "error", "0")))
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun execute(command: String): CommandResult {
        val parts = command.trim().split("\\s+".toRegex())
        val cmdName = parts.firstOrNull()?.toLowerCase(Locale.ROOT)
        val arguments = parts.drop(1)
        return commandMap[cmdName]?.invoke(arguments) ?: CommandResult(AnnotatedString("${
            LanguageUtils(
                MainApplication.getContext(),
                LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
                "unknown"
            ).getString()
        } $command"))
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
                Conventional.getString("ls", "error", "1")
            } else if (files.isEmpty()) {
                Conventional.getString("ls", "error", "2")
            } else {
                files.joinToString("\n") { it.name }
            }
        } else {
            Conventional.getString("ls", "error", "3")
        }
    }


}