package eternal.future.efmodloader.ui.screen

import androidx.compose.runtime.Composable
import eternal.future.efmodloader.ui.navigation.NavigationViewModel

expect object SettingScreen {
    @Composable
    fun SettingScreen(mainViewModel: NavigationViewModel)
}