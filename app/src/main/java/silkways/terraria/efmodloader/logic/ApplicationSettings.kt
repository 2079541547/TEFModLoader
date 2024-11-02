package silkways.terraria.efmodloader.logic

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import silkways.terraria.efmodloader.data.Settings

object ApplicationSettings {

    @JvmStatic
    fun isDarkThemeEnabled(context: Context): Boolean {
        val themeValue = JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.themeKey)

        return when (themeValue) {
            0 -> AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            1 -> false
            2 -> true

            else -> false
        }
    }

}