package me.jiangnight.tefmodloader_compose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import me.jiangnight.tefmodloader_compose.ui.screen.BottomBarDestination
import me.jiangnight.tefmodloader_compose.ui.screen.NavGraphs
import me.jiangnight.tefmodloader_compose.ui.theme.TEFModLoaderComposeTheme
import me.jiangnight.tefmodloader_compose.ui.utils.LocalSnackbarHost
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer

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
                val navController = rememberNavController() //管理应用中的导航操作
                val snackBarHostState = remember { SnackbarHostState() }//用于显示 Snackbar 消息
                //配置导航引擎，支持页面过渡动画
                val navHostEngine = rememberNavHostEngine(
                    // 将导航内容对齐到顶部
                    navHostContentAlignment = Alignment.TopCenter,
                    // 设置根导航的默认动画（进入和退出动画）
                    rootDefaultAnimations = RootNavGraphDefaultAnimations(
                        enterTransition = { fadeIn(animationSpec = tween(150)) },
                        exitTransition = { fadeOut(animationSpec = tween(150)) }
                    ),
                    // 为嵌套导航图指定动画
                    defaultAnimationsForNestedNavGraph = mapOf(
                        NavGraphs.root to NestedNavGraphDefaultAnimations(
                            enterTransition = { fadeIn(animationSpec = tween(150)) },
                            exitTransition = { fadeOut(animationSpec = tween(150)) }
                        )
                    )
                )

                // 构建屏幕布局，包含底部导航栏和 Snackbar
                Scaffold(
                    bottomBar = { BottomBar(navController) },
                    snackbarHost = { SnackbarHost(snackBarHostState) }
                ) { _ ->
                    // 提供全局 SnackbarHost，以便在子组件中使用
                    CompositionLocalProvider(LocalSnackbarHost provides snackBarHostState) {
                        // 设置导航主机，使用 DestinationsNavHost 进行页面导航
                        DestinationsNavHost(
                            modifier = Modifier.padding(0.dp),
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
private fun BottomBar(navController: NavController) {
    val navigator = navController.rememberDestinationsNavigator()
    NavigationBar(tonalElevation = 8.dp) {
        BottomBarDestination.entries.forEach { destination ->
            val isCurrentDestOnBackStack by navController.isRouteOnBackStackAsState(destination.direction)
            NavigationBarItem(selected = isCurrentDestOnBackStack, onClick = {
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
            }, icon = {
                //选中状态和未选中状态图标变化
                if (isCurrentDestOnBackStack) {
                    Icon(destination.iconSelected, stringResource(destination.label))
                } else {
                    Icon(destination.iconNotSelected, stringResource(destination.label))
                }
            }, label = {
                Text(
                    stringResource(destination.label),
                    overflow = TextOverflow.Visible,
                    maxLines = 1,
                    softWrap = false
                )
            }, alwaysShowLabel = false
            )
        }
    }
}


