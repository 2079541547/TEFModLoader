package silkways.terraria.efmodloader.ui.theme

import android.app.WallpaperManager
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import android.graphics.Color as AndroidColor

private fun adjustColor(color: Int, saturationFactor: Float = 0.8f, lightnessFactor: Float = 0.9f): Color {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(color, hsv)

    // Adjust saturation and lightness with bounds check
    // Ensure not too dull or too vibrant by setting min/max saturation
    hsv[1] = (hsv[1] * saturationFactor).coerceIn(0.4f, 0.85f) // 更严格的饱和度限制

    // Ensure not too dark or too bright by setting min/max lightness
    hsv[2] = (hsv[2] * lightnessFactor).coerceIn(0.3f, 0.9f) // 调整亮度范围

    // 如果原始颜色非常暗或非常亮，我们可以稍微增加或减少亮度以确保可读性
    if (hsv[2] < 0.2f) {
        hsv[2] = 0.3f // 对于非常暗的颜色，提升亮度到一个最小值
    } else if (hsv[2] > 0.9f) {
        hsv[2] = 0.9f // 对于非常亮的颜色，降低亮度到一个最大值
    }

    return Color(AndroidColor.HSVToColor(hsv))
}

fun createColorSchemeFromWallpaper(context: Context, isDarkTheme: Boolean): ColorScheme {

    // 获取壁纸并生成调色板
    val wallpaperManager = WallpaperManager.getInstance(context)
    val bitmap = wallpaperManager.drawable?.toBitmap()
    val palette = bitmap?.let { Palette.from(it).generate() }

    // 定义调整后的动态颜色获取方法
    fun getAdjustedSwatchColor(swatch: Palette.Swatch?, default: Color, saturationFactor: Float = 0.8f, lightnessFactor: Float = 0.9f): Color {
        return swatch?.rgb?.let {
            adjustColor(it, saturationFactor, lightnessFactor)
        } ?: default
    }

    // 创建颜色方案
    return if (isDarkTheme) {
        darkColorScheme(
            primary = getAdjustedSwatchColor(palette?.vibrantSwatch, Purple80, 0.5f, 1.5f),
            secondary = getAdjustedSwatchColor(palette?.mutedSwatch, PurpleGrey80),
            tertiary = getAdjustedSwatchColor(palette?.lightMutedSwatch, Pink80),
            onPrimary = getAdjustedSwatchColor(palette?.darkMutedSwatch, Color.Gray, 2.0f, 0.2f)
        )
    } else {
        lightColorScheme(
            primary = getAdjustedSwatchColor(palette?.vibrantSwatch, Purple40, 1.4f, 0.6f),
            secondary = getAdjustedSwatchColor(palette?.mutedSwatch, PurpleGrey40, 0.9f, 0.95f),
            tertiary = getAdjustedSwatchColor(palette?.darkVibrantSwatch, Pink40, 0.8f, 0.9f))
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
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor -> {
            createColorSchemeFromWallpaper(context, darkTheme)
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