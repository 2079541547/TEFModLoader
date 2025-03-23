package eternal.future.tefmodloader.ui.screen.about

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
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.utility.App
import eternal.future.tefmodloader.ui.AppTopBar
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.ui.widget.AboutScreen
import eternal.future.tefmodloader.ui.widget.AboutScreen.UserAgreementDialog
import eternal.future.tefmodloader.utility.Locales
import eternal.future.tefmodloader.utility.Net.openUrlInBrowser
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.jiangniaht
import tefmodloader.composeapp.generated.resources.eternalfuture
import tefmodloader.composeapp.generated.resources.morenrx

object AboutScreen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AboutScreen(mainViewModel: NavigationViewModel) {

        val locale = Locales()
        locale.loadLocalization("Screen/AboutScreen/AboutScreen.toml", Locales.getLanguage(State.language.value))

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                val menuItems = mapOf(locale.getString("exit") to Pair(Icons.AutoMirrored.Filled.ExitToApp) {
                    App.exit()
                })
                AppTopBar(
                    title = locale.getString("title"),
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
                    var n = 0
                    var physical by remember { mutableStateOf(false) }
                    AboutScreen.AppIconCard(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        labelText = locale.getString("app_content"),
                        onClick = {
                            physical = true
                            n++
                            if (n >= 25) {
                                State.screen_rollback.value = true
                            }
                        }
                    )
                }

                item {

                    val user = Locales().loadLocalization("Screen/GuideScreen/agreement.toml", Locales.getLanguage(State.language.value)).getMap()
                    val loader = Locales().loadLocalization("Screen/GuideScreen/agreement_loader.toml", Locales.getLanguage(State.language.value)).getMap()

                    var userDialog by remember { mutableStateOf(false) }
                    var loaderDialog by remember { mutableStateOf(false) }

                    if (userDialog) {
                        UserAgreementDialog(
                            title = user["agreement"].toString(),
                            content = user["agreement_content"].toString(),
                            onDismiss = { userDialog = false },
                            confirmButtonText = locale.getString("close"),
                            modifier = Modifier.padding(10.dp).fillMaxWidth()
                        )
                    }

                    if (loaderDialog) {
                        UserAgreementDialog(
                            title = loader["agreement"].toString(),
                            content = loader["agreement_content"].toString(),
                            onDismiss = { loaderDialog = false },
                            confirmButtonText = locale.getString("close"),
                            modifier = Modifier.padding(10.dp).fillMaxWidth()
                        )
                    }

                    Text(
                        "App",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )

                    var version_n by remember { mutableStateOf(0) }
                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Info,
                        title = locale.getString("version"),
                        contentDescription = "v10.0.0",
                        onClick = {
                            version_n++
                            if (version_n >= 30) {
                                State.screen_physical.value = true
                                if (version_n >= 50) {
                                    State.screen_revolve.value = true
                                }
                            }
                        }
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Info,
                        title = user["agreement"].toString(),
                        contentDescription = "",
                        onClick = {
                            userDialog = true
                        }
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Info,
                        title = loader["agreement"].toString(),
                        contentDescription = "",
                        onClick = {
                            loaderDialog = true
                        }
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.AccountBalance,
                        title = locale.getString("open_source_code"),
                        contentDescription = locale.getString("open_source_code_content"),
                        onClick = {
                            openUrlInBrowser("https://github.com/2079541547/TEFModLoader")
                        }
                    )
                }

                item {
                    Text(locale.getString("development"), modifier = Modifier.padding(10.dp))

                    AboutScreen.expandableWidget(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(Res.drawable.eternalfuture),
                        title = "EternalFuture゙",
                        detailedInfo = locale.getString("EternalFuture゙"),
                        onClick = {},
                        isCircularIcon = true
                    )
                }

                item {
                    Text(locale.getString("important_contributions"), modifier = Modifier.padding(10.dp))

                    AboutScreen.expandableWidget(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(Res.drawable.morenrx),
                        title = "MorenRx",
                        detailedInfo = locale.getString("MorenRx"),
                        onClick = {  },
                        isCircularIcon = true
                    )

                    AboutScreen.expandableWidget(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(Res.drawable.jiangniaht),
                        title = "JiangNight",
                        detailedInfo = locale.getString("JiangNight"),
                        onClick = {  },
                        isCircularIcon = true
                    )
                }

                item {
                    Text(locale.getString("more"), modifier = Modifier.padding(10.dp))

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.ThumbUp,
                        title = locale.getString("special_thanks"),
                        contentDescription = locale.getString("special_thanks_content"),
                        onClick = { mainViewModel.navigateTo("thanks") }
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Info,
                        title = locale.getString("open_source_license"),
                        contentDescription = locale.getString("open_source_license_content"),
                        onClick = { mainViewModel.navigateTo("license") }
                    )

                    AboutScreen.aboutWidgets(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.BugReport,
                        title = locale.getString("feedback"),
                        contentDescription = locale.getString("feedback_content"),
                        onClick = { openUrlInBrowser("https://github.com/2079541547/TEFModLoader/issues/new/choose") }
                    )
                }
            }
        }
    }
}