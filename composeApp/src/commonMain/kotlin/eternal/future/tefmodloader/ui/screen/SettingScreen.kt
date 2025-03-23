package eternal.future.tefmodloader.ui.screen

import androidx.compose.runtime.Composable
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel

expect object SettingScreen {
    @Composable
    fun SettingScreen(mainViewModel: NavigationViewModel)
}