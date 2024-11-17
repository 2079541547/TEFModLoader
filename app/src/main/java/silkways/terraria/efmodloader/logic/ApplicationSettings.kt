package silkways.terraria.efmodloader.logic

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.utils.SPUtils

object ApplicationSettings {

    /**
     * 检查是否启用了暗黑主题。
     *
     * @param context 应用程序上下文。
     * @return 如果启用了暗黑主题，则返回 `true`，否则返回 `false`。
     */
    @JvmStatic
    fun isDarkThemeEnabled(context: Context): Boolean {
        val themeValue = SPUtils.readInt(Settings.themeKey, -1)
        EFLog.i("读取的主题值: $themeValue")

        // 根据主题值确定是否启用暗黑主题
        val isDarkThemeEnabled = when (themeValue) {
            -1 -> {
                // 使用系统默认设置
                val defaultNightMode = AppCompatDelegate.getDefaultNightMode()
                val isSystemDarkModeEnabled = defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES
                EFLog.i("系统默认夜间模式: $isSystemDarkModeEnabled")
                isSystemDarkModeEnabled
            }
            1 -> {
                // 强制使用浅色模式
                EFLog.i("强制使用浅色模式")
                false
            }
            2 -> {
                // 强制使用暗黑模式
                EFLog.i("强制使用暗黑模式")
                true
            }
            else -> {
                // 未知主题值，默认使用浅色模式
                EFLog.w("未知主题值: $themeValue，默认使用浅色模式")
                false
            }
        }

        return isDarkThemeEnabled
    }
}