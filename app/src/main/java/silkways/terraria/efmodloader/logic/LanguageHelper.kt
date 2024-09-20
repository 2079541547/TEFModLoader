package silkways.terraria.efmodloader.logic

import android.content.Context
import android.os.LocaleList
import java.util.Locale

object LanguageHelper {

    fun getLanguageAsNumber(context: Context): Int {

        val systemLocales: LocaleList = context.resources.configuration.locales

        val primaryLocale: Locale = systemLocales.get(0)
        val language: String = primaryLocale.language
        val script: String = primaryLocale.script
        val country: String = primaryLocale.country

        var languageCode = 1

        when (language) {
            "zh" -> if ("Hans" == script) {
                languageCode = 1 // 简体中文
            } else if ("Hant" == script) {
                languageCode = if ("TW" == country || "HK" == country) {
                    2 // 繁体中文
                } else {
                    1
                }
            }

            "ru" -> languageCode = 3 // 俄语
            "en" -> languageCode = 4 // 英语
        }
        return languageCode // 如果没有匹配到任何情况
    }

    fun setAppLanguage(context: Context, languageCode: String) {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(Locale(languageCode))
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun getMDLanguage(LanguageCode: Any?, context: Context, PathName: String): String{
        val Path = "ToolBoxData/$PathName/${getLanguage(LanguageCode, context)}.md"
        return Path
    }

    fun getFileLanguage(LanguageCode: Any?, context: Context, PathName: String, file_extension: String): String{

        val Path = "ToolBoxData/$PathName/${getLanguage(LanguageCode, context)}$file_extension"

        return Path
    }


    fun getLanguage(languageCode: Any?, context: Context): String{

        var language = "zh-cn"

        when(languageCode){
            0 -> {
                language = when(getLanguageAsNumber(context)){
                    1 -> "zh-cn"
                    2 -> "zh-hant"
                    3 -> "ru"
                    4 -> "en"
                    else -> "zh-cn"
                }
            }

            1 -> language = "zh-cn"
            2 -> language = "zh-hant"
            3 -> language = "ru"
            4 -> language = "en"
            else -> language = "zh-cn"
        }

        return language
    }
}