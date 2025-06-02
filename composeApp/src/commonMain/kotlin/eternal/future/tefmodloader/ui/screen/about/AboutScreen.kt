package eternal.future.tefmodloader.ui.screen.about

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.utility.App
import eternal.future.tefmodloader.ui.AppTopBar
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.ui.widget.AboutScreen
import eternal.future.tefmodloader.utility.Locales
import eternal.future.tefmodloader.utility.Net.openUrlInBrowser
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.eternalfuture
import tefmodloader.composeapp.generated.resources.jiangniaht
import tefmodloader.composeapp.generated.resources.morenrx

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(mainViewModel: NavigationViewModel) {
    val locale = Locales()
    locale.loadLocalization("Screen/AboutScreen/AboutScreen.toml", Locales.getLanguage(State.language.value))

    // 隐藏的赞赏码弹窗状态
    var showHiddenDonation by remember { mutableStateOf(false) }
    var tapCount by remember { mutableStateOf(0) }
    val hiddenDonationTimeout = 5000L // 5秒内连续点击5次触发

    // 长按触发逻辑
    LaunchedEffect(tapCount) {
        if (tapCount > 0) {
            delay(hiddenDonationTimeout)
            tapCount = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val menuItems = mapOf(
                locale.getString("exit") to Pair(Icons.AutoMirrored.Filled.ExitToApp) {
                    App.exit()
                }
            )
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

            // 隐藏触发区域 - 完全透明
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .alpha(0.001f)
                        .clickable {
                            tapCount++
                            if (tapCount >= 20) {
                                showHiddenDonation = true
                                tapCount = 0
                            }
                        }
                ) {}
            }

            item {
                val user = Locales().loadLocalization("Screen/GuideScreen/agreement.toml", Locales.getLanguage(State.language.value)).getMap()
                val loader = Locales().loadLocalization("Screen/GuideScreen/agreement_loader.toml", Locales.getLanguage(State.language.value)).getMap()

                var userDialog by remember { mutableStateOf(false) }
                var loaderDialog by remember { mutableStateOf(false) }

                if (userDialog) {
                    AboutScreen.UserAgreementDialog(
                        title = user["agreement"].toString(),
                        content = user["agreement_content"].toString(),
                        onDismiss = { userDialog = false },
                        confirmButtonText = locale.getString("close"),
                        modifier = Modifier.padding(10.dp).fillMaxWidth()
                    )
                }

                if (loaderDialog) {
                    AboutScreen.UserAgreementDialog(
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
                    onClick = { },
                    isCircularIcon = true
                )

                AboutScreen.expandableWidget(
                    modifier = Modifier.fillMaxWidth(),
                    icon = painterResource(Res.drawable.jiangniaht),
                    title = "JiangNight",
                    detailedInfo = locale.getString("JiangNight"),
                    onClick = { },
                    isCircularIcon = true
                )
            }

            item {
                Text(locale.getString("more"), modifier = Modifier.padding(10.dp))

                AboutScreen.aboutWidgets(
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.FavoriteBorder,
                    title = locale.getString("ListOfDonors"),
                    contentDescription = locale.getString("ListOfDonors_Content"),
                    onClick = {
                        openUrlInBrowser("https://gitlab.com/2079541547/tefmodloader/-/blob/main/Document/donation.md")
                    }
                )

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
                    onClick = { openUrlInBrowser("https://gitlab.com/2079541547/tefmodloader/-/issues/new") }
                )
            }
        }

        if (showHiddenDonation) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AlertDialog(
                    onDismissRequest = { showHiddenDonation = false },
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 8.dp,
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true
                    ),
                    title = {
                        Text(
                            text = "支持我们 ❤️",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                        ) {
                            Spacer(Modifier.height(8.dp))

                            Spacer(Modifier.height(24.dp))

                            Text(
                                text = "您的支持是我们前进的动力",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = "由于某些原因请联系2079541547@qq.com捐赠\n默认捐赠将显示为匿名",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = "如需上捐赠名单，请发送邮件至：\n2079541547@qq.com\n附上您的微信名称和捐赠金额，最后是你想在捐赠名单上显示的名称",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(
                                onClick = { showHiddenDonation = false },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("我明白了", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                )
            }
        }
    }
}