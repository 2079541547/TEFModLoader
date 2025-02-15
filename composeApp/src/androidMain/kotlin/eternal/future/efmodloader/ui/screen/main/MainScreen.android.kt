package eternal.future.efmodloader.ui.screen.main

import android.os.Environment
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import eternal.future.efmodloader.MainApplication
import eternal.future.efmodloader.State
import eternal.future.efmodloader.ui.AppTopBar
import eternal.future.efmodloader.ui.navigation.BackMode
import eternal.future.efmodloader.ui.navigation.DefaultScreen
import eternal.future.efmodloader.ui.navigation.NavigationViewModel
import eternal.future.efmodloader.ui.navigation.Screen
import eternal.future.efmodloader.ui.navigation.ScreenRegistry
import eternal.future.efmodloader.utility.Locales
import java.io.File

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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    actual fun MainScreen(mainViewModel: NavigationViewModel) {

        mainScreen.loadLocalization("Screen/MainScreen/MainScreen.toml", Locales.getLanguage(State.language.value))

        val selectedItem = remember { mutableIntStateOf(0) }
        var title by remember { mutableStateOf(mainScreen.getString("home")) }

        val currentScreenWithAnimation by viewModel.currentScreen.collectAsState()



        when(State.Mode.value) {
            0 -> {
                State.EFModPath = File(
                    Environment.getExternalStorageDirectory(),
                    "Documents/TEFModLoader/Data/EFMod"
                ).path

                State.EFModLoaderPath = File(
                    Environment.getExternalStorageDirectory(),
                    "Documents/TEFModLoader/Data/EFModLoader"
                ).path
            }
            else -> {
                State.EFModPath = File(
                    MainApplication.getContext().getExternalFilesDir(null),
                    "EFMod"
                ).path

                State.EFModLoaderPath = File(
                    MainApplication.getContext().getExternalFilesDir(null),
                    "EFModLoader"
                ).path
            }
        }


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
                    title = title,
                    menuItems = menuItems
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val items = listOf(
                        Icons.Default.Home to "home",
                        Icons.Filled.Extension to "efmod",
                        Icons.Filled.Build to "loader"
                    )

                    items.forEachIndexed { index, (icon, label) ->
                        IconButton(
                            onClick = {
                                selectedItem.value = index
                                title = mainScreen.getString(label)
                                viewModel.navigateBack(BackMode.DIRECT, label)
                            },
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .weight(1f)
                                .clip(RoundedCornerShape(100.dp))
                                .then(
                                    if (selectedItem.value == index) Modifier.background(
                                        MaterialTheme.colorScheme.tertiary
                                    ) else Modifier
                                )
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