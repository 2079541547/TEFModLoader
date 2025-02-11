package eternal.future.efmodloader.ui.screen.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eternal.future.efmodloader.ui.AppTopBar
import eternal.future.efmodloader.ui.navigation.BackMode
import eternal.future.efmodloader.ui.navigation.NavigationViewModel
import eternal.future.efmodloader.ui.widget.AboutScreen
import eternal.future.efmodloader.utility.App
import eternal.future.efmodloader.utility.Net.openUrlInBrowser

object ThanksScreen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ThanksScreen(mainViewModel: NavigationViewModel) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                val menuItems = mapOf(
                    "Back to default page" to Pair(Icons.AutoMirrored.Filled.ExitToApp) { mainViewModel.navigateBack(
                        BackMode.TO_DEFAULT) },
                    "Exit" to Pair(Icons.AutoMirrored.Filled.ExitToApp) { App.exit() },
                )
                AppTopBar(
                    title = "Special thanks",
                    showMenu = true,
                    menuItems = menuItems,
                    showBackButton = true,
                    onBackClick = {
                        mainViewModel.navigateBack(BackMode.ONE_BY_ONE)
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding
            ) {
                item {
                    AboutScreen.projectInfoCard(
                        modifier = Modifier.padding(10.dp),
                        titleText = "SilkCasket",
                        descriptionText = "A compression format that emphasizes flexibility",
                        additionalInfoText = "Apache-2.0 license",
                        onClick = {
                            openUrlInBrowser("https://github.com/2079541547/SilkCasket")
                        }
                    )
                }
            }
        }
    }
}