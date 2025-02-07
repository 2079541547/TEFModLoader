package silkways.terraria.efmodloader

import androidx.compose.runtime.mutableStateOf

object State {
    val theme = mutableStateOf(configuration.getInt("theme"))
    val darkTheme = mutableStateOf(configuration.getBoolean("darkTheme"))
    val systemTheme = mutableStateOf(configuration.getBoolean("systemTheme", true))
    val language = mutableStateOf(configuration.getInt("language"))

    val selectedPath = mutableStateOf(configuration.getString("selectedPath"))

    val loggingEnabled = mutableStateOf(configuration.getBoolean("loggingEnabled", true))
    val logCache = mutableStateOf(configuration.getInt("logCache"))

    val loaderNumber = mutableStateOf(configuration.getInt("loaderNumber"))

    var isAndroid = false

    var autoPatch = mutableStateOf(true)
    var defaultLoader = mutableStateOf(true)

    var Debugging  = mutableStateOf(false)
    var SignatureKiller = mutableStateOf(0)
    var ApkPath = mutableStateOf("")
    var OverrideVersion = mutableStateOf(false)
    var Mode = mutableStateOf(configuration.getInt("Mode"))

    var SilkCasket_Temp = ""
}