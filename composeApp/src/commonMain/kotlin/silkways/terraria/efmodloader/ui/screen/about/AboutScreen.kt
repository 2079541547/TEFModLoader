package silkways.terraria.efmodloader.ui.screen.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import silkways.terraria.efmodloader.utility.App
import silkways.terraria.efmodloader.ui.AppTopBar
import silkways.terraria.efmodloader.ui.navigation.BackMode
import silkways.terraria.efmodloader.ui.navigation.NavigationViewModel
import silkways.terraria.efmodloader.ui.widget.AboutScreen
import silkways.terraria.efmodloader.ui.widget.AboutScreen.UserAgreementDialog
import silkways.terraria.efmodloader.utility.Net.openUrlInBrowser
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.jiangniaht
import tefmodloader.composeapp.generated.resources.eternalfuture
import tefmodloader.composeapp.generated.resources.morenrx

object AboutScreen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AboutScreen(mainViewModel: NavigationViewModel) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                val menuItems = mapOf("Exit" to Pair(Icons.AutoMirrored.Filled.ExitToApp) {
                    App.exit()
                })
                AppTopBar(
                    title = "About",
                    showMenu = true,
                    menuItems = menuItems,
                    showBackButton = true,
                    onBackClick = {
                            mainViewModel.navigateBack(BackMode.TO_DEFAULT)
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding
            ) {
                item {
                    AboutScreen.AppIconCard(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        labelText = "Thank you for your company"
                    )
                }

                item {
                    var userDialog by remember { mutableStateOf(false) }
                    var loaderDialog by remember { mutableStateOf(false) }

                    if (userDialog) {
                        UserAgreementDialog(
                            title = "User Agreement",
                            content = "Content of the Agreement\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n",
                            onDismiss = { userDialog = false },
                            confirmButtonText = "close",
                            modifier = Modifier.padding(10.dp).fillMaxWidth()
                        )
                    }

                    if (loaderDialog) {
                        UserAgreementDialog(
                            title = "EFModLoader Agreement",
                            content = "Content of the Agreement\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n",
                            onDismiss = { loaderDialog = false },
                            confirmButtonText = "close",
                            modifier = Modifier.padding(10.dp).fillMaxWidth()
                        )
                    }

                    Text("App", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(10.dp))
                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Info,
                        title = "Version",
                        contentDescription = "v1.0.0.0",
                        onClick = {}
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Info,
                        title = "User Agreement",
                        contentDescription = "",
                        onClick = {
                            userDialog = true
                        }
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Info,
                        title = "EFModLoader Agreement",
                        contentDescription = "Mod usage matters",
                        onClick = {
                            loaderDialog = true
                        }
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.AccountBalance,
                        title = "Open source code",
                        contentDescription = "Go to the official TEFModLoader repository",
                        onClick = {
                            openUrlInBrowser("https://github.com/2079541547/TEFModLoader")
                        }
                    )
                }

                item {
                    Text("Development", modifier = Modifier.padding(10.dp))

                    AboutScreen.expandableWidget(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(Res.drawable.eternalfuture),
                        title = "EternalFuture゙",
                        detailedInfo = "Core code, page code, page design",
                        onClick = {},
                        isCircularIcon = true
                    )
                }

                item {
                    Text("Important contributions", modifier = Modifier.padding(10.dp))

                    AboutScreen.expandableWidget(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(Res.drawable.morenrx),
                        title = "MorenRx",
                        detailedInfo = "Page design",
                        onClick = {},
                        isCircularIcon = true
                    )

                    AboutScreen.expandableWidget(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(Res.drawable.jiangniaht),
                        title = "JiangNight",
                        detailedInfo = "Program icon design",
                        onClick = {},
                        isCircularIcon = true
                    )
                }

                item {
                    Text("More", modifier = Modifier.padding(10.dp))

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.ThumbUp,
                        title = "Special thanks",
                        contentDescription = "Rankings are in chronological order only",
                        onClick = { mainViewModel.navigateTo("thanks") }
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Info,
                        title = "Open Source License",
                        contentDescription = "Check out the open source projects we use (including libraries)",
                        onClick = { mainViewModel.navigateTo("license") }
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.BugReport,
                        title = "Feedback",
                        contentDescription = "If you have encountered a bug, please click here",
                        onClick = { openUrlInBrowser("https://github.com/2079541547/TEFModLoader/issues/new/choose") }
                    )
                }
            }
        }
    }
}