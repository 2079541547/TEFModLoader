package silkways.terraria.efmodloader.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer
import silkways.terraria.efmodloader.logic.ApplicationSettings.isDarkThemeEnabled
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.efmod.LoaderManager
import silkways.terraria.efmodloader.logic.efmod.ModManager
import silkways.terraria.efmodloader.ui.screen.BottomBarDestination
import silkways.terraria.efmodloader.ui.screen.NavGraphs
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import silkways.terraria.efmodloader.ui.utils.LocalSnackbarHost
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : EFActivity() {

    private var isLoading by mutableStateOf(true)
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {

        init()
        checkPermission()

        //LoaderManager.install("/sdcard/Android/data/silkways.terraria.efmodloader/files/MyLoader.skc", "/sdcard/Android/data/silkways.terraria.efmodloader/files/EFModLoader/MyLoader")
        //ModManager.install("/sdcard/Android/data/silkways.terraria.efmodloader/MyMod.skc", "/sdcard/Android/data/silkways.terraria.efmodloader/files/EFMod/MyLoader")

        if (!File("${this.getExternalFilesDir(null)}/EFModLoader").exists()) {
            val file = File(this.externalCacheDir, "TEFModLoader.efml")
            copyAssetToFile(this, "TEFModLoader/kernel/TEFModLoader.efml", file.absolutePath)
            //install(this, file, File("${this.getExternalFilesDir(null)}/TEFModLoader/EFModLoaderData"))
            file.delete()
            try {
                // 创建文件路径
                val dir = File(getExternalFilesDir(null), "TEFModLoader/EFModLoaderData")
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val file = File(dir, "info.json")

                // 创建文件内容
                val content = """
            {
                "selectedLoaderPath": "${this.getExternalFilesDir(null)}/TEFModLoader/EFModLoaderData/TEFModLoader.efml"
            }
            """.trimIndent()

                // 写入文件
                FileWriter(file).use { writer ->
                    writer.write(content)
                }

                EFLog.i("文件创建并写入成功")
            } catch (e: IOException) {
                e.printStackTrace()
                EFLog.e("文件创建并写入失败: ${e.message}")
            }
        }

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1001)
            }

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

    private fun copyAssetToFile(context: Context, assetPath: String, destinationPath: String) {
        try {
            // 打开Assets中的文件
            val inputStream: InputStream = context.assets.open(assetPath)
            // 创建输出流，用于写入文件
            val outputStream: OutputStream = FileOutputStream(destinationPath)

            // 缓冲区大小可以适当调整
            val buffer = ByteArray(1024)
            var read: Int

            // 循环读取并写入
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }

            // 关闭流
            outputStream.close()
            inputStream.close()

            EFLog.i("文件复制成功")
        } catch (e: IOException) {
            e.printStackTrace()
            EFLog.e("文件复制失败: ${e.message}")
        }
    }


    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
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