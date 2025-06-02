package eternal.future.tefmodloader

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import eternal.future.tefmodloader.State.darkTheme
import eternal.future.tefmodloader.State.screen_physical
import eternal.future.tefmodloader.State.screen_revolve
import eternal.future.tefmodloader.State.screen_rollback
import eternal.future.tefmodloader.State.systemTheme
import eternal.future.tefmodloader.easteregg.GravityAffectedContent
import eternal.future.tefmodloader.easteregg.Screen.ClockwiseRotatingContent
import eternal.future.tefmodloader.easteregg.Screen.Rotation
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.DefaultScreen
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.ui.navigation.ScreenRegistry
import eternal.future.tefmodloader.ui.screen.HelpScreen
import eternal.future.tefmodloader.ui.screen.ModPageScreen
import eternal.future.tefmodloader.ui.screen.SettingScreen
import eternal.future.tefmodloader.ui.screen.TerminalScreen
import eternal.future.tefmodloader.ui.screen.about.AboutScreen
import eternal.future.tefmodloader.ui.screen.about.LicenseScreen
import eternal.future.tefmodloader.ui.screen.about.ThanksScreen
import eternal.future.tefmodloader.ui.screen.main.MainScreen
import eternal.future.tefmodloader.ui.screen.welcome.GuideScreen
import eternal.future.tefmodloader.ui.theme.TEFModLoaderComposeTheme
import eternal.future.tefmodloader.utility.EFMod
import java.io.File

class MainActivity : ComponentActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: Activity

        fun getContext(): Activity {
            return instance
        }

        fun exit() {
            instance.finishAffinity()
        }

        val mainViewModel = NavigationViewModel()
    }


    private var bottom = false
    private var backPressedTime = 0L
    private val backPressThreshold = 1000
    private lateinit var backCallback: OnBackPressedCallback

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()
        instance = this
        State.isAndroid = true
        enableEdgeToEdge()

        setContent {
            TEFModLoaderComposeTheme {
                initializeScreens(mainViewModel)
                NavigationHost(mainViewModel)
            }
        }

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!bottom) mainViewModel.navigateBack(BackMode.ONE_BY_ONE)
                if (backPressedTime + backPressThreshold > System.currentTimeMillis()) {
                    finishAffinity()
                } else {
                    backPressedTime = System.currentTimeMillis()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun NavigationHost(viewModel: NavigationViewModel) {
        TEFModLoaderComposeTheme(darkTheme = (if(systemTheme.value) isSystemInDarkTheme() else darkTheme.value), theme = State.theme.value) {
            val currentScreenWithAnimation by viewModel.currentScreen.collectAsState()
            val content = @Composable {
                Scaffold {
                    Crossfade(
                        targetState = currentScreenWithAnimation,
                        animationSpec = tween(durationMillis = 500)
                    ) { state ->
                        state.let { (screen, _) ->
                            if (screen != null) {
                                bottom = false
                                when (screen.id) {
                                    "welcome" -> WelcomeScreen(viewModel)
                                    "guide" -> GuideScreen.GuideScreen(viewModel)
                                    "main" -> {
                                        bottom = true
                                        MainScreen.MainScreen(viewModel)
                                    }
                                    "terminal" -> TerminalScreen.TerminalScreen(viewModel)
                                    "about" -> AboutScreen(viewModel)
                                    "help" -> HelpScreen.HelpScreen(viewModel)
                                    "license" -> LicenseScreen.LicenseScreen(viewModel)
                                    "thanks" -> ThanksScreen.ThanksScreen(viewModel)
                                    "settings" -> SettingScreen.SettingScreen(viewModel)
                                    "modpage" -> ModPageScreen.ModPageScreen(viewModel)
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            if (screen_physical.value) {
                GravityAffectedContent(
                    gravity = 100f,
                    mass = 100f,
                    elasticity = 5f,
                    containerHeight = 1000f,
                    containerWidth = 300f,
                    velocityDecay = 0.25f,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (screen_revolve.value) {
                        ClockwiseRotatingContent(-1) { content() }
                    } else content()
                }
            } else if(screen_rollback.value) Rotation { content() } else content()
        }
    }

    @Composable
    fun WelcomeScreen(viewModel: NavigationViewModel) {
        val isFirst = State.initialBoot

        LaunchedEffect(Unit) {
            if (configuration.getBoolean("externalMode", false)) {
                EFMod.update_data(
                    File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader/Data").path,
                    State.EFModPath
                )
                configuration.setBoolean("externalMode", false)
            }
        }

        viewModel.removeCurrentScreen()
        if (isFirst) viewModel.setInitialScreen("guide")
        else viewModel.setInitialScreen("main")
    }

    fun initializeScreens(viewModel: NavigationViewModel) {
        listOf(
            DefaultScreen("welcome"),
            DefaultScreen("guide"),
            DefaultScreen("main"),
            DefaultScreen("about"),
            DefaultScreen("help"),
            DefaultScreen("license"),
            DefaultScreen("thanks"),
            DefaultScreen("settings"),
            DefaultScreen("terminal"),
            DefaultScreen("modpage")
        ).forEach {
            ScreenRegistry.register(it)
        }
        viewModel.setInitialScreen("welcome")
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1001)
            }

            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = String.format("package:%s", applicationContext.packageName).toUri()
                startActivityForResult(intent, 1001)
            }
        } else {
            val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
            val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

            if (ContextCompat.checkSelfPermission(this, readPermission) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, writePermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(readPermission, writePermission), 1001)
            }
        }
    }
}