package silkways.terraria.efmodloader.ui.screen.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import silkways.terraria.efmodloader.ui.navigation.NavigationViewModel


expect object MainScreen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(mainViewModel: NavigationViewModel)
}