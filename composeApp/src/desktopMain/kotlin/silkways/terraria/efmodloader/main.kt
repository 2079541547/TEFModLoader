package silkways.terraria.efmodloader

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import silkways.terraria.efmodloader.State.darkTheme
import silkways.terraria.efmodloader.State.systemTheme
import silkways.terraria.efmodloader.debug.TerminalScreen
import silkways.terraria.efmodloader.ui.navigation.BackMode
import silkways.terraria.efmodloader.ui.navigation.DefaultScreen
import silkways.terraria.efmodloader.ui.navigation.NavigationViewModel
import silkways.terraria.efmodloader.ui.navigation.ScreenRegistry
import silkways.terraria.efmodloader.ui.screen.HelpScreen
import silkways.terraria.efmodloader.ui.screen.about.AboutScreen
import silkways.terraria.efmodloader.ui.screen.about.LicenseScreen
import silkways.terraria.efmodloader.ui.screen.about.ThanksScreen
import silkways.terraria.efmodloader.ui.screen.main.MainScreen
import silkways.terraria.efmodloader.ui.screen.welcome.GuideScreen
import silkways.terraria.efmodloader.ui.screen.welcome.welcomeScreen
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import kotlin.reflect.KFunction0

@Composable
fun NavigationHost(viewModel: NavigationViewModel) {
    TEFModLoaderComposeTheme((if(systemTheme.value) isSystemInDarkTheme() else darkTheme.value) ) {
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

fun main() = application {

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