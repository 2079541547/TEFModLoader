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

object ThanksScreen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ThanksScreen(mainViewModel: NavigationViewModel) {

        val locale = Locales().loadLocalization("Screen/AboutScreen/ThanksScreen.toml", Locales.getLanguage(State.language.value))

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
                    locale.getMap().forEach {
                        if (it.key != "title" &&
                            it.key != "exit" &&
                            it.key != "back_to_default_page") {

                            AboutScreen.projectInfoCard(
                                modifier = Modifier.padding(10.dp),
                                titleText = it.key,
                                descriptionText = it.value,
                                additionalInfoText = "",
                                onClick = { }
                            )
                        }
                    }
                }
            }
        }
    }
}