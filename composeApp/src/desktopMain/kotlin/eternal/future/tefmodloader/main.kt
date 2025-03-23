package eternal.future.tefmodloader

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import eternal.future.tefmodloader.State.darkTheme
import eternal.future.tefmodloader.State.systemTheme
import eternal.future.tefmodloader.debug.TerminalScreen
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.DefaultScreen
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.ui.navigation.ScreenRegistry
import eternal.future.tefmodloader.ui.screen.HelpScreen
import eternal.future.tefmodloader.ui.screen.about.AboutScreen
import eternal.future.tefmodloader.ui.screen.about.LicenseScreen
import eternal.future.tefmodloader.ui.screen.about.ThanksScreen
import eternal.future.tefmodloader.ui.screen.main.MainScreen
import eternal.future.tefmodloader.ui.screen.welcome.GuideScreen
import eternal.future.tefmodloader.ui.screen.welcome.welcomeScreen
import eternal.future.tefmodloader.ui.theme.TEFModLoaderComposeTheme
import eternal.future.tefmodloader.utility.App
import eternal.future.tefmodloader.utility.Zip
import java.awt.Window
import java.io.File
import kotlin.reflect.KFunction0

@Composable
fun NavigationHost(viewModel: NavigationViewModel) {
    TEFModLoaderComposeTheme(darkTheme = (if(systemTheme.value) isSystemInDarkTheme() else darkTheme.value), theme = State.theme.value) {
        val currentScreenWithAnimation by viewModel.currentScreen.collectAsState()
        Scaffold {
            Crossfade(targetState = currentScreenWithAnimation, animationSpec = tween(durationMillis = 500)) { state ->
                state.let { (screen, _) ->
                    if (screen != null) {
                        when (screen.id) {
                            "welcome" -> WelcomeScreen(viewModel)
                            "guide" -> GuideScreen.GuideScreen(viewModel)
                            "main" -> MainScreen.MainScreen(viewModel)
                            "about" -> AboutScreen.AboutScreen(viewModel)
                            "help" -> HelpScreen.HelpScreen(viewModel)
                            "license" -> LicenseScreen.LicenseScreen(viewModel)
                            "thanks" -> ThanksScreen.ThanksScreen(viewModel)
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(viewModel: NavigationViewModel) {
    val isFirst = true
    welcomeScreen {
        viewModel.removeCurrentScreen()
        if (isFirst) viewModel.setInitialScreen("guide") else viewModel.setInitialScreen("main")
    }
}

fun initializeScreens(viewModel: NavigationViewModel) {
    listOf(
        DefaultScreen("welcome"),
        DefaultScreen("guide"),
        DefaultScreen("main"),
        DefaultScreen("about"),
        DefaultScreen("help"),
        DefaultScreen("license"),
        DefaultScreen("thanks")
    ).forEach {
        ScreenRegistry.register(it)
    }
    viewModel.setInitialScreen("welcome")
}

var appState: KFunction0<Unit>? = null
fun exitApp() {
    appState?.let { it() }
}
var Window: Window? = null

@Suppress("UnsafeDynamicallyLoadedCode")
fun main() = application {

    State.EFModPath = File(App.getPrivate(), "EFMod").path
    State.EFModLoaderPath = File(App.getPrivate(), "EFModLoader").path

    File(App.getPrivate(), "SilkCasket").let {
        if (!it.exists()) {
            val osName = System.getProperty("os.name")
            val zipPath = Zip.copyZipFromResources("SilkCasket.zip", it.parent)
            if (osName.contains("nix") || osName.contains("nux")) {
                Zip.unzipSpecificFilesIgnorePath(
                    zipPath,
                    it.path,
                    "linux/libsilkcasket.so"
                )
            } else {
                Zip.unzipSpecificFilesIgnorePath(zipPath, it.path,  "windows/${App.getCurrentArchitecture()}/libsilkcasket.dll")
            }

            File(zipPath).delete()
        }
        System.load(it.path)
    }

    val mainViewModel = remember { NavigationViewModel() }

    appState = remember { ::exitApplication }

    var terminalWindowState by remember { mutableStateOf(false) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "TEFModLoader",
        onKeyEvent = { event ->
            if (event.type == KeyEventType.KeyDown) {
                when {
                    event.isCtrlPressed && event.key == Key.T -> {
                        terminalWindowState = !terminalWindowState
                        true
                    }

                    event.isCtrlPressed && event.key == Key.L -> {
                        mainViewModel.navigateBack(BackMode.ONE_BY_ONE)
                        true
                    }

                    else -> false
                }
            } else false
        }
    ) {

        Window = window

        TEFModLoaderComposeTheme {
            initializeScreens(mainViewModel)
            NavigationHost(mainViewModel)
        }

        if (terminalWindowState) {
            TerminalWindow(onClose = { terminalWindowState = false })
        }
    }
}


@Composable
fun TerminalWindow(onClose: () -> Unit) {
    Window(
        onCloseRequest = onClose,
        title = "Terminal Window",
        state = WindowState(position = WindowPosition(Alignment.Center),
            size = DpSize(800.dp, 600.dp))
    ) {
        TEFModLoaderComposeTheme(true) {
            TerminalScreen()
        }
    }
}