package eternal.future.efmodloader.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eternal.future.efmodloader.State
import eternal.future.efmodloader.State.darkTheme
import eternal.future.efmodloader.State.language
import eternal.future.efmodloader.State.loggingEnabled
import eternal.future.efmodloader.State.systemTheme
import eternal.future.efmodloader.configuration
import eternal.future.efmodloader.ui.AppTopBar
import eternal.future.efmodloader.ui.navigation.BackMode
import eternal.future.efmodloader.ui.navigation.NavigationViewModel
import eternal.future.efmodloader.ui.widget.main.SettingScreen
import eternal.future.efmodloader.utility.App
import eternal.future.efmodloader.utility.Locales

actual object SettingScreen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    actual fun SettingScreen(mainViewModel: NavigationViewModel) {

        val setting = Locales()
        setting.loadLocalization("Screen/MainScreen/SettingScreen.toml", Locales.getLanguage(State.language.value))

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                val menuItems = mapOf(setting.getString("exit") to Pair(Icons.AutoMirrored.Filled.ExitToApp) {
                    App.exit()
                })
                AppTopBar(
                    title = setting.getString("title"),
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

                    val advancedMap = mapOf(
                        0 to setting.getString("follow_system"),
                        1 to "arm64",
                        2 to "arm32",
                        3 to "x64",
                        4 to "x86"
                    )

                    Text(setting.getString("advanced"), modifier = Modifier.padding(4.dp))

                    SettingScreen.Selector(
                        title = setting.getString("architecture"),
                        defaultSelectorId = State.architecture.value,
                        selectorMap = advancedMap,
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        onClick = {
                            State.architecture.value = it
                        }
                    )

                    SettingScreen.SettingsSwitchItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        title = setting.getString("log"),
                        checked = loggingEnabled.value,
                        onCheckedChange = { check ->
                            loggingEnabled.value = check
                        },
                        iconOn = Icons.Default.BugReport
                    )

                    if (loggingEnabled.value) {

                        val logMap = mapOf(
                            512 * 1024 to "512 kb",
                            1024 * 1024 to "1024 kb",
                            2048 * 1024 to "2048 kb",
                            4096 * 1024 to "4096 kb",
                            8192 * 1024 to "8192 kb",
                            -1 to setting.getString("unlimited")
                        )

                        SettingScreen.Selector(
                            title = setting.getString("maximum_log_cache"),
                            defaultSelectorId = State.logCache.value,
                            logMap,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            onClick = {
                                State.logCache.value = it
                            }
                        )
                    }
                }

                item {
                    Text(setting.getString("general"), modifier = Modifier.fillMaxWidth().padding(4.dp))

                    val languageMap = mapOf(
                        0 to setting.getString("follow_system"),
                        1 to setting.getString("Chinese"),
                        2 to setting.getString("TraditionalChinese"),
                        // 3 to "Русский",
                        4 to setting.getString("English")
                    )

                    val themeMap = mapOf(
                        0 to Pair(setting.getString("blue"), Icons.Default.BeachAccess),
                        1 to Pair(setting.getString("red"), Icons.Default.LocalFireDepartment),
                        2 to Pair(setting.getString("green"), Icons.Default.NaturePeople),
                        3 to Pair(setting.getString("yellow"), Icons.Default.LocalFlorist),
                        4 to Pair(setting.getString("pink"), Icons.Default.Favorite),
                        5 to Pair(setting.getString("purple"), Icons.Default.Star)
                    )

                    SettingScreen.Selector(
                        title = setting.getString("language"),
                        defaultSelectorId = language.value,
                        languageMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        onClick = { select ->
                            language.value = select
                            setting.loadLocalization("Screen/SettingScreen.toml", Locales.getLanguage(select))
                            configuration.setInt("language", select)
                            mainViewModel.refreshCurrentScreen()
                        }
                    )

                    SettingScreen.selectorWithIcon(
                        title = setting.getString("theme"),
                        defaultSelectorId = State.theme.value,
                        themeMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        onClick = {
                            configuration.setInt("theme", it)
                            State.theme.value = it
                        }
                    )

                    SettingScreen.SettingsSwitchItem(
                        title = setting.getString("follow_system"),
                        contentDescription = setting.getString("followSystemContent"),
                        checked = systemTheme.value,
                        onCheckedChange = { check ->
                            systemTheme.value = check
                            configuration.setBoolean("systemTheme", check)
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        iconOn = Icons.Default.SettingsSystemDaydream
                    )

                    if (!systemTheme.value) {
                        SettingScreen.SettingsSwitchItem(
                            iconOff = Icons.Default.WbSunny,
                            iconOn = Icons.Default.NightsStay,
                            title = setting.getString("darkTheme"),
                            contentDescription = setting.getString("darkThemeContent"),
                            checked = darkTheme.value,
                            onCheckedChange = { check ->
                                darkTheme.value = check
                                configuration.setBoolean("darkTheme", check)
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}