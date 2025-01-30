package silkways.terraria.efmodloader.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import silkways.terraria.efmodloader.State.isAndroid
import silkways.terraria.efmodloader.ui.AppTopBar
import silkways.terraria.efmodloader.ui.navigation.BackMode
import silkways.terraria.efmodloader.ui.navigation.NavigationViewModel
import silkways.terraria.efmodloader.utility.App

object TerminalScreen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TerminalScreen(mainViewModel: NavigationViewModel) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (isAndroid) {
                    val menuItems = mapOf("Exit" to Pair(Icons.AutoMirrored.Filled.ExitToApp) { App.exit() })
                    AppTopBar(
                        title = "Terminal",
                        showMenu = true,
                        menuItems = menuItems,
                        showBackButton = true,
                        onBackClick = {
                            mainViewModel.navigateBack(BackMode.TO_DEFAULT)
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                silkways.terraria.efmodloader.debug.TerminalScreen()
            }
        }
    }
}