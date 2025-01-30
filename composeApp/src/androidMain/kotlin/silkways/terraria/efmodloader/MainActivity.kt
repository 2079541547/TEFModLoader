package silkways.terraria.efmodloader

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import silkways.terraria.efmodloader.State.darkTheme
import silkways.terraria.efmodloader.State.systemTheme
import silkways.terraria.efmodloader.ui.navigation.DefaultScreen
import silkways.terraria.efmodloader.ui.navigation.NavigationViewModel
import silkways.terraria.efmodloader.ui.navigation.ScreenRegistry
import silkways.terraria.efmodloader.ui.screen.HelpScreen
import silkways.terraria.efmodloader.ui.screen.SettingScreen
import silkways.terraria.efmodloader.ui.screen.TerminalScreen
import silkways.terraria.efmodloader.ui.screen.about.AboutScreen
import silkways.terraria.efmodloader.ui.screen.about.LicenseScreen
import silkways.terraria.efmodloader.ui.screen.about.ThanksScreen
import silkways.terraria.efmodloader.ui.screen.main.MainScreen
import silkways.terraria.efmodloader.ui.screen.welcome.GuideScreen
import silkways.terraria.efmodloader.ui.screen.welcome.welcomeScreen
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme

class MainActivity : ComponentActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: Activity

        fun getContext(): Activity {
            return instance
        }

        fun exit() {
            instance.finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instance = this
        State.isAndroid = true
        enableEdgeToEdge()

        setContent {
            val mainViewModel = remember { NavigationViewModel() }

            TEFModLoaderComposeTheme {
                initializeScreens(mainViewModel)
                NavigationHost(mainViewModel)
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun NavigationHost(viewModel: NavigationViewModel) {
        TEFModLoaderComposeTheme((if(systemTheme.value) isSystemInDarkTheme() else darkTheme.value) ) {
            val currentScreenWithAnimation by viewModel.currentScreen.collectAsState()
            Scaffold {
                Crossfade(
                    targetState = currentScreenWithAnimation,
                    animationSpec = tween(durationMillis = 500)
                ) { state ->
                    state.let { (screen, _) ->
                        if (screen != null) {
                            when (screen.id) {
                                "welcome" -> WelcomeScreen(viewModel)
                                "guide" -> GuideScreen.GuideScreen(viewModel)
                                "main" -> MainScreen.MainScreen(viewModel)
                                "terminal" -> TerminalScreen.TerminalScreen(viewModel)
                                "about" -> AboutScreen.AboutScreen(viewModel)
                                "help" -> HelpScreen.HelpScreen(viewModel)
                                "license" -> LicenseScreen.LicenseScreen(viewModel)
                                "thanks" -> ThanksScreen.ThanksScreen(viewModel)
                                "settings" -> SettingScreen.SettingScreen(viewModel)
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
            DefaultScreen("thanks"),
            DefaultScreen("settings"),
            DefaultScreen("terminal")
        ).forEach {
            ScreenRegistry.register(it)
        }
        viewModel.setInitialScreen("welcome")
    }
}