package silkways.terraria.toolbox.logic.terminal.logic

import android.content.Context
import android.util.JsonReader
import android.util.Log
import silkways.terraria.toolbox.data.Settings
import silkways.terraria.toolbox.logic.JsonConfigModifier
import silkways.terraria.toolbox.logic.LanguageHelper
import java.io.InputStream
import java.io.InputStreamReader

class HelpCommandHandler(private val context: Context) {

    private val helpTexts = mutableMapOf<String, String>()

    init {
        loadHelpTextsFromJson()
    }

    private fun loadHelpTextsFromJson() {
        val inputStream: InputStream = context.assets.open(
            LanguageHelper.getFileLanguage(
                JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.languageKey),
                context,
                "command",
                ".json"))
        val jsonReader = JsonReader(InputStreamReader(inputStream))
        try {
            jsonReader.beginObject()
            while (jsonReader.hasNext()) {
                val key = jsonReader.nextName()
                val value = jsonReader.nextString()
                helpTexts[key] = value
            }
            jsonReader.endObject()
        } catch (e: Exception) {
            Log.e("HelpCommandHandler", "Error reading JSON file", e)
        } finally {
            jsonReader.close()
        }
    }

    fun handleHelpCommand(parts: MutableList<String>): String {
        return if (parts.isEmpty()) {
            getAllCommandsHelp()
        } else {
            getHelpText(parts[0])
        }
    }

    private fun getAllCommandsHelp(): String {
        return helpTexts.keys.joinToString("\n") { "$it: ${helpTexts[it]}" }
    }

    private fun getHelpText(command: String): String {
        val commandKey = command.replace("-", "")
        return helpTexts[commandKey] ?: "No help available for '$command'"
    }
}