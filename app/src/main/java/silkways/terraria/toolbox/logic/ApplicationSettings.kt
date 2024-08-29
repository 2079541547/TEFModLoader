package silkways.terraria.toolbox.logic

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import silkways.terraria.toolbox.data.Settings

object ApplicationSettings {

    fun setupLanguage(context: Context) {
        var type: String = ""

        when(JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.languageKey)){
            0 -> {
                type = when(LanguageHelper.getLanguageAsNumber(context)) {
                    1, 2, 3 -> ""
                    4 -> "en"
                    else -> ({}).toString()
                }
            }
            1, 2, 3 -> type = ""
            4 -> type= "en"
        }
        LanguageHelper.setAppLanguage(context, type)
    }

    fun setupTheme(context: Context) {
        when(JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.themeKey)) {
            0 -> {
                val isDarkModeEnabled = AppCompatDelegate.getDefaultNightMode()
                if (isDarkModeEnabled == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }else if(isDarkModeEnabled == AppCompatDelegate.MODE_NIGHT_NO){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}