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

object LicenseScreen {

    data class aboutCard(
        val titleText: String,
        val descriptionText: String,
        val additionalInfoText: String,
        val url: String
    )

    val cards = listOf<aboutCard>(
        aboutCard(
            titleText = "EFModLoader",
            descriptionText = "An invasive high-efficiency mod loader designed for EFMod",
            additionalInfoText = "AGPL-3.0 license",
            url = "https://github.com/2079541547/EFModLoader"
        ),
        aboutCard(
            titleText = "BNM-Android",
            descriptionText = "ByNameModding is a library for modding il2cpp games by classes, methods, field names on Android. This edition is focused on working on Android with il2cpp. It includes everything you need for modding unity games.\nRequires C++20 minimum.",
            additionalInfoText = "MIT license",
            url = "https://github.com/ByNameModding/BNM-Android"
        ),
        aboutCard(
            titleText = "SilkCasket",
            descriptionText = "A compression format that emphasizes flexibility",
            additionalInfoText = "Apache-2.0 license",
            url = "https://github.com/2079541547/SilkCasket"
        )
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LicenseScreen(mainViewModel: NavigationViewModel) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                val menuItems = mapOf(
                    "Back to default page" to Pair(Icons.AutoMirrored.Filled.ExitToApp) { mainViewModel.navigateBack(
                        BackMode.TO_DEFAULT) },
                    "Exit" to Pair(Icons.AutoMirrored.Filled.ExitToApp) { App.exit() },
                )
                AppTopBar(
                    title = "Open Source License",
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

                items(cards.size) {
                    val card = cards[it]
                    AboutScreen.projectInfoCard(
                        modifier = Modifier.padding(10.dp),
                        titleText = card.titleText,
                        descriptionText = card.descriptionText,
                        additionalInfoText = card.additionalInfoText,
                        onClick = {
                            openUrlInBrowser(card.url)
                        }
                    )
                }
            }
        }
    }
}