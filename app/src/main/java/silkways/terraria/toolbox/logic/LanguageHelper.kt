package silkways.terraria.toolbox.logic

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

        when (language) {
            "zh" -> if ("Hans" == script) {
                return 1 // 简体中文
            } else if ("Hant" == script) {
                return if ("TW" == country || "HK" == country) {
                    2   // 繁体中文
                } else {
                    3 // 没有则使用国际语言
                }
            }

            "ru" -> return 3 // 俄语
            "en" -> return 4 // 英语
            else -> return 3 // 默认或其他语言
        }
        return 3 // 如果没有匹配到任何情况
    }

    fun setAppLanguage(context: Context, languageCode: String) {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(Locale(languageCode))
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun getMDLanguage(LanguageCode: Any?, context: Context, PathName: String): String{

       val Language_Code = when(LanguageCode){
            0 -> {
                when(getLanguageAsNumber(context)){
                    1 -> "zh-cn"
                    2 -> "zh-hant"
                    3 -> "ru"
                    4 -> "en"
                    else -> null
                }
            }

            1 -> "zh-cn"
            2 -> "zh-hant"
            3 -> "ru"
            4 -> "en"
           else -> null
       }

        val Path = "ToolBoxData/$PathName/$Language_Code.md"

        return Path
    }

    fun getFileLanguage(LanguageCode: Any?, context: Context, PathName: String, file_extension: String): String{
        val Language_Code = when(LanguageCode){
            0 -> {
                when(getLanguageAsNumber(context)){
                    1 -> "zh-cn"
                    2 -> "zh-hant"
                    3 -> "ru"
                    4 -> "en"
                    else -> null
                }
            }

            1 -> "zh-cn"
            2 -> "zh-hant"
            3 -> "ru"
            4 -> "en"
            else -> null
        }

        val Path = "ToolBoxData/$PathName/$Language_Code$file_extension"

        return Path
    }

}