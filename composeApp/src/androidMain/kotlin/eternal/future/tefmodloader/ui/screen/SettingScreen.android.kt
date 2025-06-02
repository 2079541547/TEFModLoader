package eternal.future.tefmodloader.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eternal.future.tefmodloader.MainApplication
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.State.darkTheme
import eternal.future.tefmodloader.State.language
import eternal.future.tefmodloader.State.loggingEnabled
import eternal.future.tefmodloader.State.systemTheme
import eternal.future.tefmodloader.configuration
import eternal.future.tefmodloader.ui.AppTopBar
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.ui.widget.main.SettingScreen
import eternal.future.tefmodloader.utility.App
import eternal.future.tefmodloader.utility.EFLog
import eternal.future.tefmodloader.utility.Locales
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Process
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.concurrent.TimeUnit


actual object SettingScreen {

    private fun captureLogcatAndWriteTo(outputStream: OutputStream) {
        val tempFile = createTempFile("logcat_", ".log").apply {
            deleteOnExit()
        }

        try {
            val pid = Process.myPid()
            val command = arrayOf(
                "logcat",
                "--pid=$pid",
                "-v", "threadtime",
                "-b", "all",
                "-d"
            )

            Runtime.getRuntime().exec(command).run {
                tempFile.outputStream().buffered().use { fileStream ->
                    inputStream.copyTo(fileStream)
                }

                errorStream.bufferedReader().use { errorReader ->
                    errorReader.forEachLine { line ->
                        Log.e("LogcatError", line)
                    }
                }
            }

            tempFile.inputStream().buffered().use { fileInput ->
                fileInput.copyTo(outputStream)
            }

        } catch (e: Exception) {
            Log.e("LogcatCapture", "Failed to capture logcat", e)
            throw e
        } finally {
            outputStream.flush()
            outputStream.close()
            tempFile.delete()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    actual fun SettingScreen(mainViewModel: NavigationViewModel) {

        val setting = Locales()
        setting.loadLocalization("Screen/MainScreen/SettingScreen.toml", Locales.getLanguage(language.value))

        val exportFileLauncher = rememberLauncherForActivityResult(CreateDocument("*/*")) { uri: Uri? ->
            uri?.let {
                MainApplication.getContext().contentResolver.openOutputStream(it).use { outputStream ->
                    if (outputStream != null) {
                        captureLogcatAndWriteTo(outputStream)
                    }
                }
            }
        }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        onClick = {
                            configuration.setInt("architecture", it)
                            State.architecture.value = it
                        }
                    )



                    SettingScreen.ActionButton(
                        icon = Icons.Default.Save,
                        title = setting.getString("export_logs"),
                        description = setting.getString("export_logs_content"),
                        onClick = {
                            val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
                            val fileName = "runtime-jvm-${formatter.format(Date())}.log"
                            exportFileLauncher.launch(fileName)
                        }
                    )

                }

                item {
                    Text(setting.getString("general"), modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp))

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