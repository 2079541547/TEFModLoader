package silkways.terraria.efmodloader.logic.terminal

import android.content.Context
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.logic.terminal.logic.HelpCommandHandler

class CommandParser(private val context: Context) {

   fun parseAndExecute(command: String): Any {
        val parts = command.split(" ").toMutableList()
        val operation = parts.removeAt(0)

        return when (operation) {
            "help" -> {   HelpCommandHandler(context).handleHelpCommand(parts) }

            "install -mod" -> {
                if (parts.size < 1) "???"
                else ""
            }

            "cp" -> {
                if (parts.size < 2) "Usage: cp [source] [destination]"
                else ""//cp(parts[0], parts[1])
            }
            "cp -" -> {
                if (parts.size < 2) "Usage: cp -r [source] [destination]"
                else ""//cpRecursive(parts[0], parts[1])
            }
            else -> "${context.getString(R.string.Unknown_command)} $operation"
        }
    }
}