package eternal.future.efmodloader.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eternal.future.efmodloader.State.darkTheme
import eternal.future.efmodloader.State.language
import eternal.future.efmodloader.State.loggingEnabled
import eternal.future.efmodloader.State.selectedPath
import eternal.future.efmodloader.State.systemTheme
import eternal.future.efmodloader.State.theme
import eternal.future.efmodloader.ui.navigation.NavigationViewModel
import eternal.future.efmodloader.ui.widget.main.SettingScreen
import eternal.future.efmodloader.utility.FileUtils

actual object SettingScreen {

    private val pickFolder: () -> Unit = {
        val newPath = FileUtils.openFilePicker()
        if (newPath != null) {
            selectedPath.value = newPath
        }
    }

    private val selectedArchitecture = mutableStateOf(0)
    private val architectureMap = mapOf(
        0 to "Follow the system",
        1 to "x86_64",
        2 to "x86_32",
        3 to "arm32",
        4 to "arm64"
    )

    private val languageMap = mapOf(
        0 to "English",
        1 to "Spanish",
        2 to "French",
    )

    private val themeMap = mapOf(
        0 to Pair("pink", Icons.Default.WbSunny),
        1 to Pair("blue", Icons.Default.NightsStay)
    )

    private val logMap = mapOf(
        512 to "512 kb",
        1024 to "1024 kb",
        2048 to "2048 kb",
        4096 to "4096 kb",
        8192 to "8192 kb",
        -1 to "Unlimited"
    )
    
    @Composable
    actual fun SettingScreen(mainViewModel: NavigationViewModel) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding
            ) {
                item {
                    Text("Advanced", modifier = Modifier.padding(10.dp))

                    SettingScreen.PathInputWithFilePicker(
                        title = "Choose Game Folder",
                        path = selectedPath.value,
                        onPathChange = { newPath -> selectedPath.value = newPath },
                        onFolderSelect = pickFolder,
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                    )

                    SettingScreen.Selector(
                        title = "Enforce the work structure",
                        defaultSelectorId = selectedArchitecture.value,
                        selectorMap = architectureMap,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { select ->
                            selectedArchitecture.value = select
                        }
                    )
                }

                item {
                    Text("General", modifier = Modifier.padding(10.dp))

                    SettingScreen.Selector(
                        title = "Select Language",
                        defaultSelectorId = language.value,
                        selectorMap = languageMap,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { select ->
                            language.value = select
                        }
                    )

                    SettingScreen.selectorWithIcon(
                        title = "Select Theme",
                        defaultSelectorId = 0,
                        selector = themeMap,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { select ->
                            theme.value = select
                        }
                    )

                    SettingScreen.SettingsSwitchItem(
                        title = "Follow system theme",
                        contentDescription = "Use a system color scheme",
                        checked = systemTheme.value,
                        onCheckedChange = { check ->
                            systemTheme.value = check
                        },
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        iconOn = Icons.Default.SettingsSystemDaydream
                    )

                    if (!systemTheme.value) {
                        SettingScreen.SettingsSwitchItem(
                            iconOff = Icons.Default.WbSunny,
                            iconOn = Icons.Default.NightsStay,
                            title = "Dark theme",
                            contentDescription = "Use a dark color scheme",
                            checked = darkTheme.value,
                            onCheckedChange = { check ->
                                darkTheme.value = check
                            },
                            modifier = Modifier.padding(10.dp).fillMaxWidth()
                        )
                    }
                }

                item {
                    Text("Other", modifier = Modifier.padding(10.dp))

                    SettingScreen.SettingsSwitchItem(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        title = "Log",
                        checked = loggingEnabled.value,
                        onCheckedChange = { check ->
                            loggingEnabled.value = check
                        },
                        iconOn = Icons.Default.BugReport
                    )
                    if (loggingEnabled.value) {
                        SettingScreen.Selector(
                            title = "Maximum log cache",
                            defaultSelectorId = -1,
                            selectorMap = logMap,
                            modifier = Modifier.fillMaxWidth().padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}