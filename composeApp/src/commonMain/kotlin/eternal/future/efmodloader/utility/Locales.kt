package eternal.future.efmodloader.utility

import net.peanuuutz.tomlkt.Toml
import net.peanuuutz.tomlkt.getTable
import java.util.Locale

class Locales {
    var locales = mutableMapOf<String, String>()

    fun loadLocalization(file: String, lang: String) {
        locales.clear()
        val toml = Toml.parseToTomlTable(javaClass.classLoader.getResourceAsStream("locales/$file")!!.reader().readText())

        val table = if (toml.containsKey(lang)) {
            toml.getTable(lang)
        } else {
            toml.getTable("zh-cn")
        }

        table.entries.forEach { (key, value) ->
            locales[key] = value.toString()
        }
    }

    fun getString(key: String): String {
        return locales[key] ?: key
    }

    companion object {

        fun getLanguage(code: Int): String {
            return when (code) {
                0 -> getSystem()
                1 -> "zh-cn"
                2 -> "zh-hant"
                3 -> "ru"
                4 -> "en"
                else -> "zh-cn"
            }
        }

        internal fun getSystem(): String {
            val locale = Locale.getDefault()
            val language = locale.language
            val countryOrRegion = locale.country

            return when {
                language.equals("zh", ignoreCase = true) -> {
                    when (countryOrRegion) {
                        "CN" -> "zh-cn"
                        "TW", "HK", "MO" -> "zh-hant"
                        else -> "zh"
                    }
                }
                else -> language
            }
        }
    }
}