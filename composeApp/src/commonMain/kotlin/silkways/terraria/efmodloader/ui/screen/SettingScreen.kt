package silkways.terraria.efmodloader.ui.screen

import androidx.compose.runtime.Composable
import silkways.terraria.efmodloader.ui.navigation.NavigationViewModel

expect object SettingScreen {
    @Composable
    fun SettingScreen(mainViewModel: NavigationViewModel)
}