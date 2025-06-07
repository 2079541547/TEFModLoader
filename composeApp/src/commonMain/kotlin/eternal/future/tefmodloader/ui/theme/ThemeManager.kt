package eternal.future.tefmodloader.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Color.pink
import java.awt.Color.yellow

@Composable
fun TEFModLoaderComposeTheme(
    darkMode: Int = 0,
    theme: Int = 0,
    content: @Composable () -> Unit
) {
    val themes = listOf(Ocean, Autumn, Green, Yellow, Pink, Purple)

    val isDarkTheme = when (darkMode) {
        1 -> true
        2 -> false
        else -> isSystemInDarkTheme()
    }

    val currentTheme = themes[theme.coerceIn(0, themes.lastIndex)]
    val colorScheme = if (isDarkTheme) currentTheme.darkScheme else currentTheme.lightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}