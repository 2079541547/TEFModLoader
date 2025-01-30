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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.InstallDesktop
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SettingsSystemDaydream
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
import silkways.terraria.efmodloader.State.darkTheme
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


@Composable
expect fun disposition()

object GuideScreen {

    private val viewModel = NavigationViewModel()

    init {
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
                "Exit" to Pair(Icons.AutoMirrored.Filled.ExitToApp) { App.exit() }
            )

            if (showLast.value) menuItems["Last"] = Pair(Icons.AutoMirrored.Filled.LastPage) { viewModel.navigateBack(BackMode.ONE_BY_ONE) } else menuItems.remove("Last")

            AppTopBar(
                title = "GuidePage",
                menuItems = menuItems
            )
        }) { innerPadding ->
            Crossfade(modifier = Modifier.padding(innerPadding), targetState = currentScreenWithAnimation, animationSpec = tween(durationMillis = 500)) { state ->
                state.let { (screen, _) ->
                    if (screen != null) {
                        when (screen.id) {
                            "personalize" -> personalize()
                            "disposition" -> Disposition({ disposition() })
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
    private fun personalize() {
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
                    0 to "English",
                    1 to "Spanish",
                    2 to "French",
                )

                val themeMap = mapOf(
                    0 to Pair("pink", Icons.Default.WbSunny),
                    1 to Pair("blue", Icons.Default.NightsStay)
                )

                SettingScreen.Selector(
                    title = "Select Language",
                    defaultSelectorId = 0,
                    languageMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )

                SettingScreen.selectorWithIcon(
                    title = "Select Theme",
                    defaultSelectorId = 0,
                    themeMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )

                SettingScreen.SettingsSwitchItem(
                    title = "Follow system theme",
                    contentDescription = "Use a system color scheme",
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
                        title = "Dark theme",
                        contentDescription = "Use a dark color scheme",
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
                text = { Text("Next") },
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
                    text = { Text("Next") },
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
                    text = { Text("Next") },
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
    fun disposition_2() {

        val ModeMap = mapOf(
            0 to "Exterior",
            1 to "Share",
            2 to "Inline",
            3 to "Root(risky)",
        )

        val killerMap = mapOf(
            0 to "None",
            1 to "MT Manager",
            2 to "LSPatch"
        )

        var showSelector by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            Column {
                SettingScreen.Selector(
                    title = "Select Mode",
                    defaultSelectorId = 0,
                    ModeMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = { select ->
                        showSelector = select != 3
                    }
                )

                if (showSelector) {
                    SettingScreen.ModernCheckBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        title = "Override the version code",
                        contentDescription = "Convenient downgrade operation",
                        isChecked = OverrideVersion.value,
                        onCheckedChange = { select ->
                            OverrideVersion.value = select
                        }
                    )

                    SettingScreen.ModernCheckBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        title = "Debugging",
                        contentDescription = "Install package debugging",
                        isChecked = Debugging.value,
                        onCheckedChange = { select ->
                            Debugging.value = select
                        }
                    )

                    SettingScreen.Selector(
                        title = "Signature Killer",
                        defaultSelectorId = 0,
                        killerMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        onClick = { select ->
                            SignatureKiller.value = select
                        }
                    )


                    Text(
                        "If you don't want to patch the fixation package",
                        modifier = Modifier.padding(10.dp)
                    )
                    SettingScreen.PathInputWithFilePicker(
                        title = "Select an API",
                        path = ApkPath.value,
                        onPathChange = { },
                        onFolderSelect = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    )
                }
            }

            val fabXOffset: Dp by animateDpAsState(
                targetValue = 0.dp,
                animationSpec = tween(durationMillis = 300)
            )
            var dragOffset by remember { mutableStateOf(0f) }

            ExtendedFloatingActionButton(
                text = { Text("Next") },
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next") },
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.navigateTo("agreement") },
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
                    text = { Text("Next") },
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