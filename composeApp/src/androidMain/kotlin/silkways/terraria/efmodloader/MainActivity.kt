package silkways.terraria.efmodloader

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import silkways.terraria.efmodloader.State.darkTheme
import silkways.terraria.efmodloader.State.systemTheme
import silkways.terraria.efmodloader.ui.navigation.DefaultScreen
import silkways.terraria.efmodloader.ui.navigation.NavigationViewModel
import silkways.terraria.efmodloader.ui.navigation.ScreenRegistry
import silkways.terraria.efmodloader.ui.screen.HelpScreen
import silkways.terraria.efmodloader.ui.screen.SettingScreen
import silkways.terraria.efmodloader.ui.screen.TerminalScreen
import silkways.terraria.efmodloader.ui.screen.about.AboutScreen
import silkways.terraria.efmodloader.ui.screen.about.LicenseScreen
import silkways.terraria.efmodloader.ui.screen.about.ThanksScreen
import silkways.terraria.efmodloader.ui.screen.main.MainScreen
import silkways.terraria.efmodloader.ui.screen.welcome.GuideScreen
import silkways.terraria.efmodloader.ui.screen.welcome.welcomeScreen
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import androidx.core.net.toUri
import silkways.terraria.efmodloader.utility.App
import silkways.terraria.efmodloader.utility.Zip
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

        File(App.getPrivate(), "SilkCasket").let {
            if (!it.exists()) {
                val zipPath = Zip.copyZipFromResources("SilkCasket.zip", "${it.parent}")
                Zip.unzipSpecificFilesIgnorePath(zipPath, it.path,  "android/arm64-v8a/libsilkcasket.so")
                File(zipPath).delete()
            }
            System.load(it.path)
        }

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
    }

    @Composable
    fun WelcomeScreen(viewModel: NavigationViewModel) {
        val isFirst = true
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