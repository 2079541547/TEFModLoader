package eternal.future.tefmodloader.ui.screen.about

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
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.ui.AppTopBar
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.ui.widget.AboutScreen
import eternal.future.tefmodloader.utility.App
import eternal.future.tefmodloader.utility.Locales
import eternal.future.tefmodloader.utility.Net.openUrlInBrowser

object LicenseScreen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LicenseScreen(mainViewModel: NavigationViewModel) {

        val locale = Locales().loadLocalization("Screen/AboutScreen/LicenseScreen.toml", Locales.getLanguage(State.language.value), true)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                val menuItems = mapOf(
                    locale.getString("back_to_default_page") to Pair(Icons.AutoMirrored.Filled.ExitToApp) { mainViewModel.navigateBack(
                        BackMode.TO_DEFAULT) },
                    locale.getString("exit") to Pair(Icons.AutoMirrored.Filled.ExitToApp) { App.exit() },
                )
                AppTopBar(
                    title = locale.getString("title"),
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
                    val map = locale.getMap()
                    map.forEach {
                        if (it.key != "title" &&
                            it.key != "exit" &&
                            it.key != "back_to_default_page" &&
                            it.key.split('.').size == 1) {

                            AboutScreen.projectInfoCard(
                                modifier = Modifier.padding(10.dp),
                                titleText = it.key,
                                descriptionText = map[it.key].toString(),
                                additionalInfoText = map["${it.key}.additionalInfoText"].toString(),
                                onClick = {
                                    openUrlInBrowser(map["${it.key}.url"].toString())
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}