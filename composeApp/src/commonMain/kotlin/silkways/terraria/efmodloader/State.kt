package silkways.terraria.efmodloader

import androidx.compose.runtime.mutableStateOf

object State {
    val theme = mutableStateOf(0)
    val darkTheme = mutableStateOf(true)
    val systemTheme = mutableStateOf(false)
    val language = mutableStateOf(0)

    val selectedPath = mutableStateOf("")

    val loggingEnabled = mutableStateOf(true)
    val logCache = mutableStateOf(-1)

    val loaderNumber = mutableStateOf(0)

    var isAndroid = false
}