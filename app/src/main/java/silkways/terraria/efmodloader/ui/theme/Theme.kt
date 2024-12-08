package silkways.terraria.efmodloader.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.app.WallpaperManager
import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette

fun createColorSchemeFromWallpaper(context: Context): ColorScheme {
    return try {
        val wallpaperManager = WallpaperManager.getInstance(context)
        val bitmap = wallpaperManager.drawable?.toBitmap()

        if (bitmap != null) {
            val palette = Palette.from(bitmap).generate()
            lightColorScheme(
                primary = Color(palette.getVibrantColor(Color.LTGRAY).toArgb()),
                secondary = Color(palette.getMutedColor(Color.GRAY).toArgb()),
                tertiary = Color(palette.getDarkVibrantColor(Color.DKGRAY).toArgb())
                // 根据需要添加更多属性
            )
        } else {
            LightColorScheme
        }
    } catch (e: Exception) {
        // 如果遇到任何问题，回退到默认颜色方案
        LightColorScheme
    }
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TEFModLoaderComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}