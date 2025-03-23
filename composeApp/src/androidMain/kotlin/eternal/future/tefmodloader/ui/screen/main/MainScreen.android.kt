package eternal.future.tefmodloader.ui.screen.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.ui.AppTopBar
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.DefaultScreen
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.ui.navigation.Screen
import eternal.future.tefmodloader.ui.navigation.ScreenRegistry
import eternal.future.tefmodloader.utility.Locales

actual object MainScreen {

    val viewModel = NavigationViewModel()

    init {
        listOf(
            DefaultScreen("home"),
            DefaultScreen("efmod"),
            DefaultScreen("loader")
        ).forEach {
            ScreenRegistry.register(it)
        }
        viewModel.setInitialScreen("home")
    }

    val title = mutableStateOf("home")

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    actual fun MainScreen(mainViewModel: NavigationViewModel) {

        val currentScreenWithAnimation by viewModel.currentScreen.collectAsState()
        mainScreen.loadLocalization("Screen/MainScreen/MainScreen.toml", Locales.getLanguage(State.language.value))

        Scaffold(
            topBar = {
                val menuItems = mutableMapOf(
                    mainScreen.getString("about") to Pair(Icons.Default.Info) { mainViewModel.navigateTo("about") },
                    mainScreen.getString("help") to Pair(Icons.AutoMirrored.Filled.Help) { mainViewModel.navigateTo("help") },
                    mainScreen.getString("settings") to Pair(Icons.Default.Settings) { mainViewModel.navigateTo("settings") },
                    mainScreen.getString("terminal") to Pair(Icons.Default.Terminal) { mainViewModel.navigateTo("terminal") },
                    mainScreen.getString("exit") to Pair(Icons.AutoMirrored.Filled.ExitToApp) { mainViewModel.navigateBack(BackMode.ONE_BY_ONE) }
                )
                AppTopBar(
                    title = mainScreen.getString(title.value),
                    menuItems = menuItems
                )
            },
            bottomBar = {

                val items = listOf(
                    Icons.Default.Home to "home",
                    Icons.Filled.Extension to "efmod",
                    Icons.Filled.Build to "loader"
                )

                NavigationBar {
                    items.forEachIndexed { route, (icon, label) ->
                        NavigationBarItem(
                            selected = title.value == label,
                            onClick = {
                                title.value = label
                                viewModel.navigateTo(label)
                                      },
                            icon = {
                                Icon(
                                    icon,
                                    contentDescription = label,
                                )
                            },
                            label = {
                                Text(mainScreen.getString(label))
                            },
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        ) { innerPadding ->
            Crossfade(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                targetState = currentScreenWithAnimation, animationSpec = tween(durationMillis = 500)
            ) { state ->
                state.let { (screen, _) ->
                    if (screen != null) {
                        when (screen.id) {
                            "home" -> {
                                HomeScreen.HomeScreen()
                                title.value = "home"
                            }
                            "efmod" -> {
                                EFModScreen.EFModScreen()
                                title.value = "efmod"
                            }
                            "loader" -> {
                                LoaderScreen.LoaderScreen()
                                title.value = "loader"
                            }
                            else -> UnknownScreen(screen)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun UnknownScreen(screen: Screen) {
        Column {
            Text("Unknown screen: ${screen.id}")
            Button(modifier = Modifier,
                content = {
                    Text("Back to default")
                },
                onClick = {
                    viewModel.navigateBack(BackMode.TO_DEFAULT)
                }
            )
        }
    }
}