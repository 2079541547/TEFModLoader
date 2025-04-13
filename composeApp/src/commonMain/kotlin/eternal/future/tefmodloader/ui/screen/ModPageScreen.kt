package eternal.future.tefmodloader.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eternal.future.tefmodloader.State.isAndroid
import eternal.future.tefmodloader.ui.AppTopBar
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import java.io.File

object ModPageScreen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun ModPageScreen(mainViewModel: NavigationViewModel) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (isAndroid) {
                    AppTopBar(
                        title = mainViewModel.getExtraData("title") as String,
                        showMenu = false,
                        showBackButton = true,
                        onBackClick = {
                            mainViewModel.navigateBack(BackMode.TO_DEFAULT)
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                eternal.future.tefmodloader.utility.loadPageFromFile(
                    File(mainViewModel.getExtraData("page-path") as String),
                    mainViewModel.getExtraData("page-class") as String,
                    mainViewModel.getExtraData("page-extraData") as Map<String, Any>)
            }
        }
    }

}