package eternal.future.efmodloader

import androidx.compose.runtime.mutableStateOf
import eternal.future.efmodloader.utility.App
import java.io.File

object State {
    val theme = mutableStateOf(configuration.getInt("theme"))
    val darkTheme = mutableStateOf(configuration.getBoolean("darkTheme"))
    val systemTheme = mutableStateOf(configuration.getBoolean("systemTheme", true))
    val language = mutableStateOf(configuration.getInt("language"))

    val selectedPath = mutableStateOf(configuration.getString("selectedPath"))

    val loggingEnabled = mutableStateOf(configuration.getBoolean("loggingEnabled", true))
    val logCache = mutableStateOf(configuration.getInt("logCache", -1))

    val loaderNumber = mutableStateOf(configuration.getInt("loaderNumber"))

    var isAndroid = false

    var autoPatch = mutableStateOf(true)
    var defaultLoader = mutableStateOf(true)

    var Debugging  = mutableStateOf(false)
    var SignatureKiller = mutableStateOf(0)
    var ApkPath = mutableStateOf("")
    var OverrideVersion = mutableStateOf(false)
    var Mode = mutableStateOf(configuration.getInt("Mode"))
    var gamePack = mutableStateOf(true)

    var SilkCasket_Temp = File(App.getPrivate(), "SilkCasket_Temp").path

    var EFModPath = ""
    var EFModLoaderPath = ""

    var screen_physical = mutableStateOf(false)
    var screen_rollback = mutableStateOf(false)
    var screen_revolve = mutableStateOf(false)

    val architecture = mutableStateOf(configuration.getInt("architecture"))
    val patched = mutableStateOf(configuration.getBoolean("patched", false))
}