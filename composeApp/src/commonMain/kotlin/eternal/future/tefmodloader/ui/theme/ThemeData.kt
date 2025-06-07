package eternal.future.tefmodloader.ui.theme

import androidx.compose.material3.ColorScheme

sealed class ThemeData {
    abstract val lightScheme: ColorScheme
    abstract val darkScheme: ColorScheme
}