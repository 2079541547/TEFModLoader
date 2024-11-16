package silkways.terraria.efmodloader.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.Coil
import coil.ImageLoader
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.isRouteOnBackStackAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer
import silkways.terraria.efmodloader.ui.screen.BottomBarDestination
import silkways.terraria.efmodloader.ui.screen.NavGraphs
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import silkways.terraria.efmodloader.ui.utils.LocalSnackbarHost

class MainActivity : EFActivity() {
    private var isLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {

        init()

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)


        splashScreen.setKeepOnScreenCondition { isLoading }

        setContent {
            TEFModLoaderComposeTheme {
                val navController = rememberNavController()
                val snackBarHostState = remember { SnackbarHostState() }
                val navHostEngine = rememberNavHostEngine(
                    navHostContentAlignment = Alignment.TopCenter,
                    rootDefaultAnimations = RootNavGraphDefaultAnimations(
                        enterTransition = { fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) },
                        exitTransition = { fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) }
                    ),
                    defaultAnimationsForNestedNavGraph = mapOf(
                        NavGraphs.root to NestedNavGraphDefaultAnimations(
                            enterTransition = { fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) },
                            exitTransition = { fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) }
                        )
                    )
                )

                Scaffold(
                    bottomBar = { BottomBar(navController, this) },
                    snackbarHost = { SnackbarHost(snackBarHostState) }
                ) { paddingValues ->
                    CompositionLocalProvider(LocalSnackbarHost provides snackBarHostState) {
                        DestinationsNavHost(
                            modifier = Modifier.padding(paddingValues),
                            navGraph = NavGraphs.root,
                            navController = navController,
                            engine = navHostEngine
                        )
                    }
                }
            }
        }
        val context = this
        val iconSize = resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
        Coil.setImageLoader(
            ImageLoader.Builder(context)
                .components {
                    add(AppIconKeyer())
                    add(AppIconFetcher.Factory(iconSize, false, context))
                }
                .build()
        )

        isLoading = false
    }

}

@Composable
private fun BottomBar(navController: NavController, context: Context) {
    val navigator = navController.rememberDestinationsNavigator()
    NavigationBar(tonalElevation = 8.dp) {
        BottomBarDestination.init(context)
        BottomBarDestination.entries.forEach { destination ->
            val isCurrentDestOnBackStack by navController.isRouteOnBackStackAsState(destination.direction)
            NavigationBarItem(
                selected = isCurrentDestOnBackStack,
                onClick = {
                    if (isCurrentDestOnBackStack) {
                        navigator.popBackStack(destination.direction, false)
                    }
                    navigator.navigate(destination.direction) {
                        popUpTo(NavGraphs.root) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        if (isCurrentDestOnBackStack) destination.iconSelected else destination.iconNotSelected,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        destination.label,
                        overflow = TextOverflow.Visible,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}