package silkways.terraria.efmodloader.logic

import android.content.Context
import android.os.LocaleList
import java.util.Locale

object LanguageHelper {

    /**
     * 获取系统语言的编号。
     *
     * @param context 应用程序上下文。
     * @return 语言编号，1表示简体中文，2表示繁体中文，3表示俄语，4表示英语，其他值表示默认语言。
     */
    fun getLanguageAsNumber(context: Context): Int {
        val systemLocales: LocaleList = context.resources.configuration.locales
        val primaryLocale: Locale = systemLocales[0]
        val language: String = primaryLocale.language
        val script: String = primaryLocale.script
        val country: String = primaryLocale.country

        var languageCode = 1 // 默认为中文(简体)

        when (language) {
            "zh" -> if (script == "Hans") {
                languageCode = 1 // 简体中文
            } else if (script == "Hant") {
                languageCode = if (country == "TW" || country == "HK") {
                    2 // 繁体中文
                } else {
                    1 // 默认为简体中文
                }
            }

            "ru" -> languageCode = 3 // 俄语
            "en" -> languageCode = 4 // 英语
            "ja" -> languageCode = 5 // 日语
            "ko" -> languageCode = 6 // 韩语
            "it" -> languageCode = 7 // 意大利语
            "es" -> languageCode = 8 // 西班牙语
            "fr" -> languageCode = 9 // 法语
            "de" -> languageCode = 10 // 德语
            else -> EFLog.i("未识别的语言: $language")
        }

        return languageCode
    }


    /**
     * 设置应用程序的语言。
     *
     * @param context 应用程序上下文。
     * @param languageCode 语言代码，例如 "zh-CN"。
     */
    fun setAppLanguage(context: Context, languageCode: String) {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(Locale(languageCode))
        resources.updateConfiguration(configuration, resources.displayMetrics)
        EFLog.i("应用程序语言已设置为: $languageCode")
    }

    /**
     * 获取 Markdown 文件的路径。
     *
     * @param LanguageCode 语言代码，可以是数字或字符串。
     * @param context 应用程序上下文。
     * @param PathName 路径名称。
     * @return Markdown 文件的路径。
     */
    fun getMDLanguage(LanguageCode: Any?, context: Context, PathName: String): String {
        val Path = "TEFModLoader/$PathName/${getLanguage(LanguageCode, context)}.md"
        EFLog.i("Markdown 文件路径: $Path")
        return Path
    }

    /**
     * 获取带有特定扩展名的文件路径。
     *
     * @param LanguageCode 语言代码，可以是数字或字符串。
     * @param context 应用程序上下文。
     * @param PathName 路径名称。
     * @param file_extension 文件扩展名。
     * @return 文件路径。
     */
    fun getFileLanguage(LanguageCode: Any?, context: Context, PathName: String, file_extension: String): String {
        val Path = "TEFModLoader/$PathName/${getLanguage(LanguageCode, context)}$file_extension"
        EFLog.i("文件路径: $Path")
        return Path
    }

    /**
     * 获取语言代码。
     *
     * @param languageCode 语言代码，可以是数字或字符串。
     * @param context 应用程序上下文。
     * @return 语言代码字符串，例如 "zh-cn"。
     */
    fun getLanguage(languageCode: Any?, context: Context): String {
        var language = ""

        when (languageCode) {
            0 -> {
                language = when (getLanguageAsNumber(context)) {
                    1 -> "zh-cn"
                    2 -> "zh-hant"
                    3 -> "ru"
                    4 -> "en"
                    5 -> "ja"
                    6 -> "ko"
                    7 -> "it"
                    8 -> "es"
                    9 -> "fr"
                    10 -> "de"
                    else -> "zh-cn"
                }
            }
            1 -> language = "zh-cn"
            2 -> language = "zh-hant"
            3 -> language = "ru"
            4 -> language = "en"
            5 -> language = "ja"
            6 -> language = "ko"
            7 -> language = "it"
            8 -> language = "es"
            9 -> language = "fr"
            10 -> language = "de"
            else -> language = "zh-cn"
        }

        EFLog.i("获取到的语言代码: $language")
        return language
    }

}