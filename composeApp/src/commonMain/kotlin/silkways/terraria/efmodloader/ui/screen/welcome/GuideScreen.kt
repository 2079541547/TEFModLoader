package silkways.terraria.efmodloader.ui.screen.welcome

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.InstallDesktop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import silkways.terraria.efmodloader.State
import silkways.terraria.efmodloader.State.darkTheme
import silkways.terraria.efmodloader.State.language
import silkways.terraria.efmodloader.State.loggingEnabled
import silkways.terraria.efmodloader.State.systemTheme
import silkways.terraria.efmodloader.ui.AppTopBar
import silkways.terraria.efmodloader.ui.navigation.BackMode
import silkways.terraria.efmodloader.ui.navigation.DefaultScreen
import silkways.terraria.efmodloader.ui.navigation.NavigationViewModel
import silkways.terraria.efmodloader.ui.navigation.ScreenRegistry
import silkways.terraria.efmodloader.ui.widget.main.SettingScreen
import silkways.terraria.efmodloader.ui.widget.welcome.GuideScreen
import silkways.terraria.efmodloader.utility.App
import silkways.terraria.efmodloader.utility.Locales

object GuideScreen {

    val viewModel = NavigationViewModel()
    val locales = Locales()

    init {
        locales.loadLocalization("GuideScreen.toml", locales.getSystem())

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

    var autoPatch = mutableStateOf(true)
    var defaultLoader = mutableStateOf(true)

    var Debugging  = mutableStateOf(false)
    var SignatureKiller = mutableStateOf(0)
    var ApkPath = mutableStateOf("")
    var OverrideVersion = mutableStateOf(false)

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
                            "disposition" -> Disposition({ silkways.terraria.efmodloader.ui.screen.welcome.GuideScreen.disposition() })
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
        LaunchedEffect(key1 = Unit) {
            showLast.value = false
        }

        val fabXOffset: Dp by animateDpAsState(
            targetValue = 0.dp ,
            animationSpec = tween(durationMillis = 300)
        )
        var dragOffset by remember { mutableStateOf(0f) }

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
                    0 to locales.getString("followSystem"),
                    1 to "简体中文",
                    2 to "繁體中文",
                    // 3 to "Русский",
                    4 to "English"
                )

                val themeMap = mapOf(
                    0 to Pair("沧海明月", Icons.Default.BeachAccess),
                    1 to Pair("朱雀烈阳", Icons.Default.LocalFireDepartment),
                    2 to Pair("翠竹幽林", Icons.Default.NaturePeople),
                    3 to Pair("金秋稻香", Icons.Default.LocalFlorist),
                    4 to Pair("桃花春水", Icons.Default.Favorite),
                    5 to Pair("紫气东来", Icons.Default.Star)
                )

                SettingScreen.Selector(
                    title = locales.getString("language"),
                    defaultSelectorId = language.value,
                    languageMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = { select ->
                        language.value = select
                        locales.loadLocalization("GuideScreen.toml", locales.getLanguage(select))
                        mainViewModel.refreshCurrentScreen()
                    }
                )

                SettingScreen.selectorWithIcon(
                    title = locales.getString("theme"),
                    defaultSelectorId = State.theme.value,
                    themeMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {
                        State.theme.value = it
                    }
                )

                SettingScreen.SettingsSwitchItem(
                    title = locales.getString("followSystem"),
                    contentDescription = locales.getString("followSystemContent"),
                    checked = systemTheme.value,
                    onCheckedChange = { check ->
                        systemTheme.value = check
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    iconOn = Icons.Default.SettingsSystemDaydream
                )

                if (!systemTheme.value) {
                    SettingScreen.SettingsSwitchItem(
                        iconOff = Icons.Default.WbSunny,
                        iconOn = Icons.Default.NightsStay,
                        title = locales.getString("darkTheme"),
                        contentDescription = locales.getString("darkThemeContent"),
                        checked = darkTheme.value,
                        onCheckedChange = { check ->
                            darkTheme.value = check
                        },
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    )
                }
            }
            ExtendedFloatingActionButton(
                text = { Text(locales.getString("next")) },
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next") },
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.navigateTo("disposition") },
                modifier = Modifier
                    .offset(x = with(LocalDensity.current) { (fabXOffset.value + dragOffset).toDp() })
                    .align(Alignment.BottomEnd)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            dragOffset += dragAmount.x
                            change.consume()
                        }
                    }
                    .graphicsLayer(
                        translationX = dragOffset
                    )
                    .padding(20.dp)
            )
        }
    }

    @Composable
    private fun agreement() {
        val showNext = remember { mutableStateOf(false) }
        val fabXOffset: Dp by animateDpAsState(
            targetValue = 0.dp,
            animationSpec = tween(durationMillis = 300)
        )
        var dragOffset by remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                GuideScreen.AgreementCard(
                    title = "User Agreement",
                    agreementText = "Content of the Agreement",
                    checkBoxTitle = "I agree to the above agreement",
                    onCheckBoxChange = { check ->
                        showNext.value = check
                    }
                )
            }
            if (showNext.value) {
                ExtendedFloatingActionButton(
                    text = { Text(locales.getString("next")) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { viewModel.navigateTo("agreement_loader") },
                    modifier = Modifier
                        .offset(x = with(LocalDensity.current) { (fabXOffset.value + dragOffset).toDp() })
                        .align(Alignment.BottomEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                dragOffset += dragAmount.x
                                change.consume()
                            }
                        }
                        .graphicsLayer(
                            translationX = dragOffset
                        )
                        .padding(20.dp)
                )
            }
        }
    }

    @Composable
    fun agreement_loader(mainViewModel: NavigationViewModel) {

        LaunchedEffect(key1 = Unit) {
            showLast.value = false
        }

        val showNext = remember { mutableStateOf(false) }
        val fabXOffset: Dp by animateDpAsState(
            targetValue = 0.dp,
            animationSpec = tween(durationMillis = 300)
        )
        var dragOffset by remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                GuideScreen.AgreementCard(
                    title = "EFModLoader uses the protocol",
                    agreementText = "Content of the Agreement",
                    checkBoxTitle = "I agree to the above agreement",
                    onCheckBoxChange = { check ->
                        showNext.value = check
                    }
                )
            }
            if (showNext.value) {
                ExtendedFloatingActionButton(
                    text = { Text(locales.getString("next")) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        mainViewModel.setInitialScreen("main")
                        mainViewModel.navigateTo("main")
                    },
                    modifier = Modifier
                        .offset(x = with(LocalDensity.current) { (fabXOffset.value + dragOffset).toDp() })
                        .align(Alignment.BottomEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                dragOffset += dragAmount.x
                                change.consume()
                            }
                        }
                        .graphicsLayer(
                            translationX = dragOffset
                        )
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

        val fabXOffset: Dp by animateDpAsState(
            targetValue = 0.dp,
            animationSpec = tween(durationMillis = 300)
        )
        var dragOffset by remember { mutableStateOf(0f) }

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
                    title = "Install the default loader",
                    contentDescription = "If you're a, tick it",
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
                    title = "Log",
                    checked = loggingEnabled.value,
                    onCheckedChange = { check ->
                        loggingEnabled.value = check
                    },
                    iconOn = Icons.Default.BugReport
                )
                if (loggingEnabled.value) {

                    val logMap = mapOf(
                        0 to "512 kb",
                        1 to "1024 kb",
                        2 to "2048 kb",
                        3 to "4096 kb",
                        4 to "8192 kb",
                        5 to "Unlimited"
                    )

                    SettingScreen.Selector(
                        title = "Maximum log cache",
                        defaultSelectorId = 5,
                        logMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    )
                }
            }

            if (showNext_disposition.value) {
                ExtendedFloatingActionButton(
                    text = { Text(locales.getString("next")) },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next"
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if (!autoPatch.value) {
                            viewModel.navigateTo("disposition_2")
                        } else {
                            viewModel.navigateTo("agreement")
                        }
                    },
                    modifier = Modifier
                        .offset(x = with(LocalDensity.current) { (fabXOffset.value + dragOffset).toDp() })
                        .align(Alignment.BottomEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                dragOffset += dragAmount.x
                                change.consume()
                            }
                        }
                        .graphicsLayer(
                            translationX = dragOffset
                        )
                        .padding(20.dp)
                )
            }
        }
    }
}


@Composable
expect fun silkways.terraria.efmodloader.ui.screen.welcome.GuideScreen.disposition()

@Composable
expect fun silkways.terraria.efmodloader.ui.screen.welcome.GuideScreen.disposition_2()