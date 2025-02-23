package eternal.future.efmodloader.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eternal.future.efmodloader.State.isAndroid
import eternal.future.efmodloader.ui.AppTopBar
import eternal.future.efmodloader.ui.navigation.BackMode
import eternal.future.efmodloader.ui.navigation.NavigationViewModel
import eternal.future.efmodloader.utility.App

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
                eternal.future.efmodloader.debug.TerminalScreen()
            }
        }
    }
}