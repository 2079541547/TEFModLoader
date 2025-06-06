package eternal.future.tefmodloader.ui.screen.welcome

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.InstallDesktop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.State.autoPatch
import eternal.future.tefmodloader.State.darkMode
import eternal.future.tefmodloader.State.defaultLoader
import eternal.future.tefmodloader.State.language
import eternal.future.tefmodloader.State.loggingEnabled
import eternal.future.tefmodloader.configuration
import eternal.future.tefmodloader.ui.AppTopBar
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.DefaultScreen
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.ui.navigation.ScreenRegistry
import eternal.future.tefmodloader.ui.widget.main.SettingScreen
import eternal.future.tefmodloader.ui.widget.welcome.GuideScreen
import eternal.future.tefmodloader.utility.App
import eternal.future.tefmodloader.utility.EFLog
import eternal.future.tefmodloader.utility.EFModLoader
import eternal.future.tefmodloader.utility.Locales
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt


object GuideScreen {

    val viewModel = NavigationViewModel()
    val locales = Locales()
    val disposition = Locales()

    init {
        locales.loadLocalization("Screen/GuideScreen/GuideScreen.toml", Locales.getLanguage(language.value))
        listOf(
            DefaultScreen("personalize"),
            DefaultScreen("disposition"),
            DefaultScreen("agreement"),
            DefaultScreen("agreement_loader"),
            DefaultScreen("disposition_2")
            ).forEach {
            ScreenRegistry.register(it)
        }
        viewModel.setInitialScreen("personalize")
    }

    val showNext_disposition = mutableStateOf(false)

    private val showLast = mutableStateOf(false)


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GuideScreen(mainViewModel: NavigationViewModel) {
        val currentScreenWithAnimation by viewModel.currentScreen.collectAsState()
        Scaffold(topBar = {
            val menuItems = mutableMapOf(
                locales.getString("exit") to Pair(Icons.AutoMirrored.Filled.ExitToApp) { App.exit() }
            )

            if (showLast.value) menuItems[locales.getString("last")] = Pair(Icons.AutoMirrored.Filled.LastPage) { viewModel.navigateBack(BackMode.ONE_BY_ONE) } else menuItems.remove("Last")

            AppTopBar(
                title = locales.getString("title"),
                menuItems = menuItems
            )
        }) { innerPadding ->
            Crossfade(modifier = Modifier.padding(innerPadding), targetState = currentScreenWithAnimation, animationSpec = tween(durationMillis = 500)) { state ->
                state.let { (screen, _) ->
                    if (screen != null) {
                        when (screen.id) {
                            "personalize" -> personalize(mainViewModel)
                            "disposition" -> Disposition({ eternal.future.tefmodloader.ui.screen.welcome.GuideScreen.disposition() })
                            "disposition_2" -> disposition_2()
                            "agreement" -> agreement()
                            "agreement_loader" -> agreement_loader(mainViewModel = mainViewModel)
                            else -> {  }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun personalize(mainViewModel: NavigationViewModel) {

        val personalize = Locales()
        personalize.loadLocalization("Screen/GuideScreen/personalize.toml", Locales.getLanguage(language.value))

        LaunchedEffect(key1 = Unit) {
            showLast.value = false
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                val languageMap = mapOf(
                    0 to personalize.getString("followSystem"),
                    1 to personalize.getString("Chinese"),
                    2 to personalize.getString("TraditionalChinese"),
                    // 3 to "Русский",
                    4 to personalize.getString("English")
                )

                val themeMap = mapOf(
                    0 to Pair(personalize.getString("blue"), Icons.Default.BeachAccess),
                    1 to Pair(personalize.getString("red"), Icons.Default.LocalFireDepartment),
                    2 to Pair(personalize.getString("green"), Icons.Default.NaturePeople),
                    3 to Pair(personalize.getString("yellow"), Icons.Default.LocalFlorist),
                    4 to Pair(personalize.getString("pink"), Icons.Default.Favorite),
                    5 to Pair(personalize.getString("purple"), Icons.Default.Star)
                )

                val darkModeMap = mapOf(
                    0 to personalize.getString("followSystem"),
                    1 to personalize.getString("darkMode_enable"),
                    2 to personalize.getString("darkMode_disable"),
                )

                SettingScreen.Selector(
                    title = personalize.getString("language"),
                    defaultSelectorId = language.value,
                    languageMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = { select ->
                        language.value = select
                        locales.loadLocalization("Screen/GuideScreen/GuideScreen.toml", Locales.getLanguage(select))
                        configuration.setInt("language", select)
                        mainViewModel.refreshCurrentScreen()
                    }
                )

                SettingScreen.selectorWithIcon(
                    title = personalize.getString("theme"),
                    defaultSelectorId = State.theme.value,
                    themeMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {
                        configuration.setInt("theme", it)
                        State.theme.value = it
                    }
                )

                SettingScreen.Selector(
                    title = personalize.getString("darkMode"),
                    defaultSelectorId = darkMode.value,
                    darkModeMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = { select ->
                        darkMode.value = select
                        locales.loadLocalization("Screen/GuideScreen/GuideScreen.toml", Locales.getLanguage(select))
                        configuration.setInt("darkMode", select)
                    }
                )

            }

            Button(
                onClick = { viewModel.navigateTo("disposition") },
                content = { Text(locales.getString("next")) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            )
        }
    }

    @Composable
    private fun agreement() {

        val agreement = Locales()
        agreement.loadLocalization("Screen/GuideScreen/agreement.toml", Locales.getLanguage(language.value))

        val showNext = remember { mutableStateOf(false) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                GuideScreen.AgreementCard(
                    title = agreement.getString("agreement"),
                    agreementText = agreement.getString("agreement_content"),
                    checkBoxTitle = agreement.getString("above_agreement"),
                    onCheckBoxChange = { check ->
                        showNext.value = check
                    }
                )
            }
            if (showNext.value) {
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }
                ExtendedFloatingActionButton(
                    text = { Text(locales.getString("next")) },
                    icon = { Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { viewModel.navigateTo("agreement_loader") },
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                offsetX.roundToInt(),
                                offsetY.roundToInt()
                            )
                        }
                        .align(Alignment.BottomEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        .padding(20.dp)
                )
            }
        }
    }

    @Composable
    fun agreement_loader(mainViewModel: NavigationViewModel) {

        val agreement = Locales()
        agreement.loadLocalization("Screen/GuideScreen/agreement_loader.toml", Locales.getLanguage(language.value))

        LaunchedEffect(key1 = Unit) {
            showLast.value = false
        }

        val showNext = remember { mutableStateOf(false) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                GuideScreen.AgreementCard(
                    title = agreement.getString("agreement"),
                    agreementText = agreement.getString("agreement_content"),
                    checkBoxTitle = agreement.getString("above_agreement"),
                    onCheckBoxChange = { check ->
                        showNext.value = check
                    }
                )
            }
            if (showNext.value) {
                var offsetX by remember { mutableFloatStateOf(0f) }
                var offsetY by remember { mutableFloatStateOf(0f) }
                val scope = rememberCoroutineScope()

                ExtendedFloatingActionButton(
                    text = { Text(locales.getString("next")) },
                    icon = { Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        scope.launch {
                            if (defaultLoader.value) {
                                try {
                                    withContext(Dispatchers.IO) {
                                        val tempFile = File.createTempFile("TEFModLoader", ".efml")
                                        val target = File(State.EFModLoaderPath, "default")

                                        FileOutputStream(tempFile).use { fileOutputStream ->
                                            javaClass.classLoader?.getResourceAsStream("tefmodloader.efml")?.copyTo(fileOutputStream)
                                        }

                                        EFModLoader.install(tempFile.path, target.path)
                                        File(target, "enabled").mkdirs()
                                        tempFile.delete()
                                    }
                                } catch (e: IOException) {
                                    EFLog.e("安装默认加载器时出现错误：", e)
                                }
                            }

                            // 导航操作回到主线程
                            withContext(Dispatchers.Main) {
                                mainViewModel.setInitialScreen("main")
                                mainViewModel.navigateTo("main")
                            }
                        }
                    },
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                offsetX.roundToInt(),
                                offsetY.roundToInt()
                            )
                        }
                        .align(Alignment.BottomEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        .padding(20.dp)
                )
            }
        }
    }



    @Composable
    fun Disposition(UI: @Composable () -> Unit) {
        LaunchedEffect(key1 = Unit) {
            showLast.value = true
        }

        disposition.loadLocalization("Screen/GuideScreen/disposition.toml", Locales.getLanguage(language.value))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                UI()

                SettingScreen.ModernCheckBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    title = disposition.getString("install_loader"),
                    contentDescription = disposition.getString("install_loader_content"),
                    isChecked = defaultLoader.value,
                    onCheckedChange = { check ->
                        defaultLoader.value = check
                    },
                    icon = Icons.Default.InstallDesktop
                )

                SettingScreen.SettingsSwitchItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    title = disposition.getString("log"),
                    checked = loggingEnabled.value,
                    onCheckedChange = { check ->
                        loggingEnabled.value = check
                    },
                    iconOn = Icons.Default.BugReport
                )

                if ( loggingEnabled.value) {
                    val logMap = mapOf(
                        512 * 1024 to "512 kb",
                        1024 * 1024 to "1024 kb",
                        2048 * 1024 to "2048 kb",
                        4096 * 1024 to "4096 kb",
                        8192 * 1024 to "8192 kb",
                        -1 to disposition.getString("unlimited")
                    )

                    SettingScreen.Selector(
                        title = disposition.getString("maximum_log_cache"),
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

            if (showNext_disposition.value) {
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }
                ExtendedFloatingActionButton(
                    text = { Text(locales.getString("next")) },
                    icon = { Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if (!autoPatch.value) {
                            viewModel.navigateTo("disposition_2")
                        } else {
                            viewModel.navigateTo("agreement")
                        }
                    },
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                offsetX.roundToInt(),
                                offsetY.roundToInt()
                            )
                        }
                        .align(Alignment.BottomEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        .padding(20.dp)
                )
            }
        }
    }
}


@Composable
expect fun eternal.future.tefmodloader.ui.screen.welcome.GuideScreen.disposition()

@Composable
expect fun eternal.future.tefmodloader.ui.screen.welcome.GuideScreen.disposition_2()