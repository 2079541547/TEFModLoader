package eternal.future.efmodloader

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
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import eternal.future.efmodloader.State.darkTheme
import eternal.future.efmodloader.State.screen_physical
import eternal.future.efmodloader.State.screen_revolve
import eternal.future.efmodloader.State.screen_rollback
import eternal.future.efmodloader.State.systemTheme
import eternal.future.efmodloader.easteregg.GravityAffectedContent
import eternal.future.efmodloader.easteregg.Screen.ClockwiseRotatingContent
import eternal.future.efmodloader.easteregg.Screen.Rotation
import eternal.future.efmodloader.ui.navigation.DefaultScreen
import eternal.future.efmodloader.ui.navigation.NavigationViewModel
import eternal.future.efmodloader.ui.navigation.ScreenRegistry
import eternal.future.efmodloader.ui.screen.HelpScreen
import eternal.future.efmodloader.ui.screen.SettingScreen
import eternal.future.efmodloader.ui.screen.TerminalScreen
import eternal.future.efmodloader.ui.screen.about.AboutScreen
import eternal.future.efmodloader.ui.screen.about.LicenseScreen
import eternal.future.efmodloader.ui.screen.about.ThanksScreen
import eternal.future.efmodloader.ui.screen.main.MainScreen
import eternal.future.efmodloader.ui.screen.welcome.GuideScreen
import eternal.future.efmodloader.ui.screen.welcome.welcomeScreen
import eternal.future.efmodloader.ui.theme.TEFModLoaderComposeTheme
import eternal.future.efmodloader.utility.App
import eternal.future.efmodloader.utility.EFMod
import eternal.future.efmodloader.utility.Zip
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
    }



    @SuppressLint("UnsafeDynamicallyLoadedCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()
        instance = this
        State.isAndroid = true
        enableEdgeToEdge()

        setContent {
            val mainViewModel = remember { NavigationViewModel() }
            TEFModLoaderComposeTheme {
                initializeScreens(mainViewModel)
                NavigationHost(mainViewModel)
            }
        }
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
                                when (screen.id) {
                                    "welcome" -> WelcomeScreen(viewModel)
                                    "guide" -> GuideScreen.GuideScreen(viewModel)
                                    "main" -> MainScreen.MainScreen(viewModel)
                                    "terminal" -> TerminalScreen.TerminalScreen(viewModel)
                                    "about" -> AboutScreen.AboutScreen(viewModel)
                                    "help" -> HelpScreen.HelpScreen(viewModel)
                                    "license" -> LicenseScreen.LicenseScreen(viewModel)
                                    "thanks" -> ThanksScreen.ThanksScreen(viewModel)
                                    "settings" -> SettingScreen.SettingScreen(viewModel)
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

        if (configuration.getBoolean("externalMode", false)) {
            EFMod.update_data(
                File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader/Data").path,
                State.EFModPath
            )
            configuration.setBoolean("externalMode", false)
        }

        welcomeScreen {
            viewModel.removeCurrentScreen()
            if (isFirst) viewModel.setInitialScreen("guide") else viewModel.setInitialScreen("main")
        }
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
            DefaultScreen("terminal")
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