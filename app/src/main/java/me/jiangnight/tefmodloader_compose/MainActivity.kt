package me.jiangnight.tefmodloader_compose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import me.jiangnight.tefmodloader_compose.ui.theme.TEFModLoaderComposeTheme

class MainActivity : ComponentActivity() {
    private var isLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() //android12以前的

        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        splashScreen.setKeepOnScreenCondition {isLoading}
        enableEdgeToEdge()
        setContent {
            TEFModLoaderComposeTheme {

            }
        }
    }
}

