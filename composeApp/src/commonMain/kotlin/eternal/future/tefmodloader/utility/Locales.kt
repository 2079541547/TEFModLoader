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
        EFLog.d("开始加载本地化文件: $file, 语言: $lang")
        locales.clear()
        val stream = javaClass.classLoader?.getResourceAsStream("locales/$file")
        if (stream == null) {
            EFLog.e("无法找到资源文件: locales/$file")
            return this
        }
        val tomlString = stream.reader().readText()
        val toml = Toml.parseToTomlTable(tomlString)

        if (loadzh) {
            EFLog.i("已加载zh-cn用于补全")
            toml.getTable("zh-cn").entries.forEach { (key, value) ->
                locales[key] = value.toString()
            }
        }

        val table = if (toml.containsKey(lang)) {
            EFLog.i("找到了指定语言的表格: $lang")
            toml.getTable(lang)
        } else {
            EFLog.w("未找到指定语言的表格: $lang, 使用默认语言: zh-cn")
            toml.getTable("zh-cn")
        }

        table.entries.forEach { (key, value) ->
            locales[key] = value.toString()
        }
        EFLog.d("完成加载本地化文件: $file, 语言: $lang")
        return this
    }

    fun getString(key: String): String {
        val result = locales[key]

        if (result == null) {
            EFLog.w("未找到键为 '$key' 的本地化字符串，使用默认值：'$key'")
            return key
        } else {
            return result
        }
    }

    companion object {

        fun getLanguage(code: Int): String {
            EFLog.d("根据代码获取语言: $code")
            return when (code) {
                0 -> {
                    val systemLang = getSystem()
                    EFLog.d("系统语言为: $systemLang")
                    systemLang
                }
                1 -> {
                    EFLog.d("选择的语言为: zh-cn")
                    "zh-cn"
                }
                2 -> {
                    EFLog.d("选择的语言为: zh-hant")
                    "zh-hant"
                }
                3 -> {
                    EFLog.d("选择的语言为: ru")
                    "ru"
                }
                4 -> {
                    EFLog.d("选择的语言为: en")
                    "en"
                }
                else -> {
                    EFLog.w("未知语言代码: $code, 默认返回: zh-cn")
                    "zh-cn"
                }
            }
        }

        internal fun getSystem(): String {
            val locale = Locale.getDefault()
            val language = locale.language
            val countryOrRegion = locale.country

            EFLog.v("当前系统语言: $language, 国家或地区: $countryOrRegion")

            return when {
                language.equals("zh", ignoreCase = true) -> {
                    when (countryOrRegion) {
                        "CN" -> {
                            EFLog.d("确定系统语言为: zh-cn")
                            "zh-cn"
                        }
                        "TW", "HK", "MO" -> {
                            EFLog.d("确定系统语言为: zh-hant")
                            "zh-hant"
                        }
                        else -> {
                            EFLog.w("未识别的中文区域, 默认返回: zh")
                            "zh"
                        }
                    }
                }
                else -> {
                    EFLog.d("非中文系统语言: $language")
                    language
                }
            }
        }
    }
}