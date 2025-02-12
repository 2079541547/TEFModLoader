package eternal.future.efmodloader.ui.screen.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import eternal.future.efmodloader.State
import eternal.future.efmodloader.debug.TerminalScreen
import eternal.future.efmodloader.ui.AppTopBar
import eternal.future.efmodloader.ui.navigation.BackMode
import eternal.future.efmodloader.ui.navigation.DefaultScreen
import eternal.future.efmodloader.ui.navigation.NavigationViewModel
import eternal.future.efmodloader.ui.navigation.Screen
import eternal.future.efmodloader.ui.navigation.ScreenRegistry
import eternal.future.efmodloader.ui.screen.SettingScreen
import eternal.future.efmodloader.utility.App
import eternal.future.efmodloader.utility.Locales

actual object MainScreen {

    private val viewModel = NavigationViewModel()

    init {
        listOf(
            DefaultScreen("home"),
            DefaultScreen("manager"),
            DefaultScreen("toolbox"),
            DefaultScreen("efmod"),
            DefaultScreen("loader"),
            DefaultScreen("settings"),
            DefaultScreen("terminal")
        ).forEach {
            ScreenRegistry.register(it)
        }
        viewModel.setInitialScreen("home")
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    actual fun MainScreen(mainViewModel: NavigationViewModel) {

        mainScreen.loadLocalization("Screen/MainScreen/MainScreen.toml", Locales.getLanguage(State.language.value))

        val selectedItem = remember { mutableStateOf(0) }
        var title by remember { mutableStateOf("home") }

        val currentScreenWithAnimation by viewModel.currentScreen.collectAsState()
        Scaffold(topBar = {
            val menuItems = mutableMapOf(
                mainScreen.getString("about") to Pair(Icons.Default.Info) { mainViewModel.navigateTo("about") },
                mainScreen.getString("help") to Pair(Icons.AutoMirrored.Filled.Help) { mainViewModel.navigateTo("help") },
                mainScreen.getString("exit") to Pair(Icons.AutoMirrored.Filled.ExitToApp) { App.exit() }
            )
            AppTopBar(
                title = title,
                menuItems = menuItems
            )
        }) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                Surface(
                    modifier = Modifier
                        .width(80.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 4.dp
                ) {
                    LazyColumn(modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp)) {
                        listOf(
                            Icons.Default.Home to "home",
                            Icons.Filled.Extension to "efmod",
                            Icons.Filled.Build to "loader",
                            Icons.Default.Settings to "settings",
                            Icons.Default.Terminal to "terminal",
                        ).forEachIndexed { index, (icon, label) ->
                            item {
                                IconButton(
                                    onClick = {
                                        selectedItem.value = index
                                        title = mainScreen.getString(label)
                                        viewModel.navigateBack(BackMode.DIRECT, label)
                                    },
                                    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                                        .clip(RoundedCornerShape(100.dp))
                                        .then(if (selectedItem.value == index) Modifier.background(MaterialTheme.colorScheme.tertiary) else Modifier)
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = label,
                                        tint = if (selectedItem.value == index) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                Crossfade(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    targetState = currentScreenWithAnimation, animationSpec = tween(durationMillis = 500)
                ) { state ->
                    state.let { (screen, _) ->
                        if (screen != null) {
                            when (screen.id) {
                                "terminal" -> TerminalScreen()
                                "settings" -> SettingScreen.SettingScreen(mainViewModel)
                                "home" -> HomeScreen.HomeScreen()
                                "efmod" -> EFModScreen.EFModScreen()
                                "loader" -> LoaderScreen.LoaderScreen()
                                else -> UnknownScreen(screen)
                            }
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