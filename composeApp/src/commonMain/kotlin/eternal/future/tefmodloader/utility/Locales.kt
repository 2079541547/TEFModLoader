package eternal.future.tefmodloader.utility

import net.peanuuutz.tomlkt.Toml
import net.peanuuutz.tomlkt.getTable
import java.util.Locale

class Locales {
    var locales = mutableMapOf<String, String>()

    fun getMap(): Map<String, String> {
        return locales
    }

    fun loadLocalization(file: String, lang: String, loadzh: Boolean = false): Locales {
        locales.clear()
        val stream = javaClass.classLoader?.getResourceAsStream("locales/$file")
        if (stream == null) {
            EFLog.e("无法找到资源文件: locales/$file")
            return this
        }
        val tomlString = stream.reader().readText()
        val toml = Toml.parseToTomlTable(tomlString)

        if (loadzh) {
            toml.getTable("zh-cn").entries.forEach { (key, value) ->
                locales[key] = value.toString()
            }
        }

        val table = if (toml.containsKey(lang)) {
            toml.getTable(lang)
        } else {
            toml.getTable("zh-cn")
        }

        table.entries.forEach { (key, value) ->
            locales[key] = value.toString()
        }
        return this
    }

    fun getString(key: String): String {
        val result = locales[key]

        if (result == null) {
            return key
        } else {
            return result
        }
    }

    companion object {

        fun getLanguage(code: Int): String {
            return when (code) {
                0 -> {
                    val systemLang = getSystem()
                    systemLang
                }
                1 -> {
                    "zh-cn"
                }
                2 -> {
                    "zh-hant"
                }
                3 -> {
                    "ru"
                }
                4 -> {
                    "en"
                }
                else -> {
                    "zh-cn"
                }
            }
        }

        internal fun getSystem(): String {
            val locale = Locale.getDefault()
            val language = locale.language
            val countryOrRegion = locale.country


            return when {
                language.equals("zh", ignoreCase = true) -> {
                    when (countryOrRegion) {
                        "CN" -> {
                            "zh-cn"
                        }
                        "TW", "HK", "MO" -> {
                            "zh-hant"
                        }
                        else -> {
                            "zh"
                        }
                    }
                }
                else -> {
                    language
                }
            }
        }
    }
}