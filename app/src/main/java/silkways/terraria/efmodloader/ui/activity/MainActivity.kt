package silkways.terraria.efmodloader.ui.activity

import android.content.Context
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer
import silkways.terraria.efmodloader.logic.ApplicationSettings.isDarkThemeEnabled
import silkways.terraria.efmodloader.ui.screen.BottomBarDestination
import silkways.terraria.efmodloader.ui.screen.NavGraphs
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import silkways.terraria.efmodloader.ui.utils.LocalSnackbarHost
import java.io.File

class MainActivity : EFActivity() {

    private var isLoading by mutableStateOf(true)
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {

        init()
        checkPermission()

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { isLoading }

        setContent {
            TEFModLoaderComposeTheme(darkTheme = isDarkThemeEnabled(this)) {
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
        startFolderCheck()
    }

    private fun startFolderCheck() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(500)
                checkAndDeleteFolder()
            }
        }
    }

    private fun checkAndDeleteFolder() {
        val folderPath = "${this.externalCacheDir}/Reboot" // 替换为你的文件夹路径
        val folder = File(folderPath)

        if (folder.exists()) {
            deleteFolder(folder)
            recreate() // 重启Activity
        }
    }

    private fun deleteFolder(folder: File) {
        if (folder.isDirectory) {
            folder.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    deleteFolder(file)
                } else {
                    file.delete()
                }
            }
        }
        folder.delete()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // 取消协程任务
    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
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



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 所有权限都被授予
            } else {
                // 至少有一个权限被拒绝
                checkPermission()
            }
        }
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