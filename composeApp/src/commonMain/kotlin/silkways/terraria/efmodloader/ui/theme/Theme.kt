package silkways.terraria.efmodloader.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TEFModLoaderComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: Int = 0,
    content: @Composable () -> Unit
) {

    var DarkColorScheme = silkways.terraria.efmodloader.ui.theme.ocean.darkScheme
    var LightColorScheme = silkways.terraria.efmodloader.ui.theme.ocean.lightScheme

    when(theme) {
        1 -> {
            DarkColorScheme = silkways.terraria.efmodloader.ui.theme.autumn.darkScheme
            LightColorScheme = silkways.terraria.efmodloader.ui.theme.autumn.lightScheme
        }
        2 -> {
            DarkColorScheme = silkways.terraria.efmodloader.ui.theme.green.darkScheme
            LightColorScheme = silkways.terraria.efmodloader.ui.theme.green.lightScheme
        }
        3 -> {
            DarkColorScheme = silkways.terraria.efmodloader.ui.theme.yellow.darkScheme
            LightColorScheme = silkways.terraria.efmodloader.ui.theme.yellow.lightScheme
        }
        4 -> {
            DarkColorScheme = silkways.terraria.efmodloader.ui.theme.pink.darkScheme
            LightColorScheme = silkways.terraria.efmodloader.ui.theme.pink.lightScheme
        }
        5 -> {
            DarkColorScheme = silkways.terraria.efmodloader.ui.theme.purple.darkScheme
            LightColorScheme = silkways.terraria.efmodloader.ui.theme.purple.lightScheme
        }
        else -> {}
    }

    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}