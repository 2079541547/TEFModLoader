package eternal.future.tefmodloader.ui.screen.main

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import eternal.future.Loader
import eternal.future.tefmodloader.MainActivity
import eternal.future.tefmodloader.MainApplication
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.State.ApkPath
import eternal.future.tefmodloader.State.Debugging
import eternal.future.tefmodloader.State.Mode
import eternal.future.tefmodloader.State.OverrideVersion
import eternal.future.tefmodloader.State.gamePack
import eternal.future.tefmodloader.configuration
import eternal.future.tefmodloader.ui.screen.welcome.GuideScreen
import eternal.future.tefmodloader.ui.screen.welcome.Patch
import eternal.future.tefmodloader.ui.widget.main.HomeScreen
import eternal.future.tefmodloader.utility.Apk
import eternal.future.tefmodloader.utility.EFLog
import eternal.future.tefmodloader.utility.EFMod
import eternal.future.tefmodloader.utility.FileUtils
import eternal.future.tefmodloader.utility.Locales
import eternal.future.tefmodloader.utility.copyApk
import eternal.future.tefmodloader.utility.doesAnyAppContainMetadata
import eternal.future.tefmodloader.utility.extractWithPackageName
import eternal.future.tefmodloader.utility.getPackageNamesWithMetadata
import eternal.future.tefmodloader.utility.getSupportedAbi
import eternal.future.tefmodloader.utility.launchAppByPackageName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.inline_game
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.math.roundToInt

actual object HomeScreen{

    val hasUnityDataFile = run {
        runCatching { Class.forName("com.unity3d.player.UnityPlayerActivity") }.isSuccess
    }

    @Composable
    actual fun HomeScreen() {

        val locale = Locales().loadLocalization("Screen/MainScreen/HomeScreen.toml", Locales.getLanguage(State.language.value))
        val updateLog = Locales().loadLocalization("Screen/MainScreen/UpDatedContent.toml", Locales.getLanguage(State.language.value))

        val snackbarHostState = SnackbarHostState()
        val scope: CoroutineScope = rememberCoroutineScope()

        var showExportDialog by remember { mutableStateOf(false) }
        var showPatchDialog by remember { mutableStateOf(false) }
        var showPatchingDialog by remember { mutableStateOf(false) }
        var showLaunchDialog by remember { mutableStateOf(false) }

        val exportFileLauncher = rememberLauncherForActivityResult(CreateDocument("application/vnd.android.package-archive")) { uri: Uri? ->
            uri?.let { documentUri ->
                try {
                    val context = MainApplication.getContext()
                    val apkFile = File(context.getExternalFilesDir(null), "patch/Game.apk")

                    context.contentResolver.openOutputStream(documentUri)?.use { outputStream ->
                        FileInputStream(apkFile).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    apkFile.delete()
                    configuration.setBoolean("patched", false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        if (State.initialBoot) {

            var showPatchedDialog by remember { mutableStateOf(true) }

            if (showPatchedDialog) {
                AlertDialog(
                    onDismissRequest = { /* 不允许关闭对话框防止误触 */ },
                    title = { Text(locale.getString("patched")) },
                    text = {
                        Column {
                            Text(locale.getString("patched_content"))
                        }
                    },

                    confirmButton = {
                        TextButton(onClick = {
                            showPatchingDialog = true
                            State.initialBoot = false
                            configuration.setBoolean("initialBoot", false)
                            showPatchedDialog = false
                            State.autoPatch.value = false
                        }) {
                            Text(locale.getString("determine"))
                        }
                    },

                    dismissButton = {
                        TextButton(onClick = {
                            State.initialBoot = false
                            configuration.setBoolean("initialBoot", false)
                            showPatchedDialog = false
                            State.autoPatch.value = false
                        }) {
                            Text(locale.getString("cancel"))
                        }
                    }
                )
            }
        }

        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text(locale.getString("export_the_file")) },
                text = {
                    Column {
                        Text(locale.getString("export_the_file_content"))
                    } },
                confirmButton = {
                    TextButton(onClick = {
                        exportFileLauncher.launch("patch.APK")
                        showExportDialog = false
                    }) {
                        Text(locale.getString("export"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        File(MainApplication.getContext().getExternalFilesDir(null), "patch/Game.apk").delete()
                        showExportDialog = false
                    }) {
                        Text(locale.getString("cancel_and_delete_the_file"))
                    }
                }
            )
        }

        if (showPatchDialog) {
            AlertDialog(
                onDismissRequest = { showPatchDialog = false },
                title = { Text(locale.getString("patch")) },
                text = {
                    GuideScreen.Patch()
                },
                confirmButton = {
                    TextButton(onClick = {
                        State.autoPatch.value = false
                        showPatchDialog = false
                        showPatchingDialog = true
                    }) {
                        Text(locale.getString("determine"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        Mode.value = 0
                        Debugging.value = false
                        OverrideVersion.value = false
                        gamePack.value = false
                        ApkPath.value = ""
                        State.autoPatch.value = false
                        showPatchDialog = false
                    }) {
                        Text(locale.getString("cancel_and_clear_the_configuration"))
                    }
                }
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(snackbarData = data)
                }
            },
        ) { a -> a


            if (showPatchingDialog) {
                AlertDialog(
                    onDismissRequest = { /* 不允许关闭对话框直到修补完成 */ },
                    title = { Text(locale.getString("patching")) },
                    text = {
                        Column {
                            Text(locale.getString("patching_content"))
                            CircularProgressIndicator()
                        }
                    },
                    confirmButton = {}
                )

                LaunchedEffect(showPatchingDialog) {
                    if (showPatchingDialog) {
                        withContext(Dispatchers.IO) {
                            val target = File(MainApplication.getContext().getExternalFilesDir(null), "patch/game.apk")

                            if (State.autoPatch.value) {
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                    Mode.value = 1
                                }
                                Apk.extractWithPackageName("com.and.games505.TerrariaPaid", target.path)
                            }

                            if (ApkPath.value == "") {
                                Apk.extractWithPackageName(
                                    packageName = "com.and.games505.TerrariaPaid",
                                    targetPath = target.path
                                )
                            } else {
                                target.parentFile?.mkdirs()
                                Apk.copyApk(
                                    ApkPath.value,
                                    target.path
                                )
                            }

                            if (!target.exists()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(locale.getString("patch_error_0"))
                                }
                            } else {

                                Apk.patch(
                                    apkPath = File(
                                        MainApplication.getContext().getExternalFilesDir(null),
                                        "patch/game.apk"
                                    ).path,
                                    outPath = File(
                                        MainApplication.getContext().getExternalFilesDir(null),
                                        "patch/game.apk"
                                    ).path,
                                    mode = Mode.value,
                                    bypass = State.isBypass.value,
                                    debug = Debugging.value,
                                    overrideVersion = OverrideVersion.value,
                                )
                            }

                            showPatchingDialog = false
                        }
                    }
                }
            }

            var isLoading by remember { mutableStateOf(false) }
            var initializationError by remember { mutableStateOf<String?>(null) }
            val coroutineScope = rememberCoroutineScope()

            if (showLaunchDialog) {
                EFLog.d("显示游戏启动对话框")

                AlertDialog(
                    onDismissRequest = {
                        if (!isLoading) {
                            EFLog.v("用户关闭了启动对话框")
                            showLaunchDialog = false
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isLoading) {
                                EFLog.v("显示加载指示器")
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 8.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            Text(locale.getString("launch_the_menu"), style = MaterialTheme.typography.headlineSmall)
                        }
                    },
                    text = {
                        Column {
                            initializationError?.let { error ->
                                EFLog.e("初始化错误: $error")
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            LazyColumn {
                                val game = Apk.getPackageNamesWithMetadata("TEFModLoader")
                                EFLog.d("获取到 ${game.size} 个支持的游戏包")

                                if (hasUnityDataFile) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = !isLoading) {
                                                    EFLog.d("用户点击了内联系包")
                                                    coroutineScope.launch {
                                                        try {
                                                            isLoading = true
                                                            initializationError = null
                                                            EFLog.i("开始初始化内联游戏")

                                                            withContext(Dispatchers.IO) {
                                                                EFLog.d("使用内联模式初始化")
                                                                EFLog.d("检测到Unity数据文件，执行特殊初始化")

                                                                File(
                                                                    MainApplication.getContext().filesDir,
                                                                    "TEFModLoader"
                                                                ).let {
                                                                    if (it.exists()) {
                                                                        FileUtils.deleteDirectory(it)
                                                                    }
                                                                }

                                                                EFMod.initialize(
                                                                    State.EFModPath,
                                                                    State.EFModLoaderPath,
                                                                    File(
                                                                        MainApplication.getContext().filesDir,
                                                                        "TEFModLoader"
                                                                    ).path,
                                                                    "arm64-v8a"
                                                                )

                                                                eternal.future.State.Mode =
                                                                    1
                                                                eternal.future.State.Bypass =
                                                                    false
                                                                eternal.future.State.EFMod_c =
                                                                    State.EFModPath;

                                                                eternal.future.State.Modx =
                                                                    File(
                                                                        MainApplication.getContext().filesDir,
                                                                        "TEFModLoader/Modx"
                                                                    )

                                                                eternal.future.State.EFMod =
                                                                    File(
                                                                        MainApplication.getContext().filesDir,
                                                                        "TEFModLoader/EFMod"
                                                                    )

                                                                EFLog.d("Unity数据初始化完成")

                                                                eternal.future.State.gameActivity =
                                                                    Class.forName("com.unity3d.player.UnityPlayerActivity")

                                                                val gameActivity =
                                                                    Intent(
                                                                        MainApplication.getContext(),
                                                                        eternal.future.State.gameActivity
                                                                    ).apply {
                                                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                    }

                                                                MainApplication.getContext()
                                                                    .startActivity(
                                                                        gameActivity
                                                                    )

                                                                Loader.initialize()
                                                            }
                                                        } catch (e: Exception) {
                                                            val errorMsg =
                                                                locale.getString("initialization_failed") + ": ${e.localizedMessage}"
                                                            EFLog.e("初始化失败: $errorMsg", e)
                                                            initializationError = errorMsg
                                                            isLoading = false
                                                        }
                                                    }
                                                }
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = painterResource(Res.drawable.inline_game),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .padding(end = 16.dp)
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = MainApplication.getContext().packageName,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${locale.getString("mode")} ${locale.getString("inline")}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    text = "1.4.4.9.6",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }

                                item {
                                    game.forEach { (packageName, mode) ->
                                        EFLog.v("处理游戏包: $packageName, 模式: $mode")
                                        val icon: Drawable? = remember(packageName) {
                                            try {
                                                EFLog.d("尝试获取应用图标: $packageName")
                                                MainApplication.getContext().packageManager.getApplicationIcon(packageName)
                                            } catch (e: PackageManager.NameNotFoundException) {
                                                EFLog.w("获取应用图标失败: ${e.message}")
                                                null
                                            }
                                        }

                                        val versionInfo = remember(packageName) {
                                            try {
                                                EFLog.d("尝试获取版本信息: $packageName")
                                                MainApplication.getContext().packageManager.getPackageInfo(
                                                    packageName,
                                                    PackageManager.GET_META_DATA
                                                ).versionName ?: locale.getString("unknown_version")
                                            } catch (e: Exception) {
                                                EFLog.w("获取版本信息失败: ${e.message}")
                                                locale.getString("unknown_version")
                                            }
                                        }

                                        val modeString = when (mode) {
                                            0 -> locale.getString("external")
                                            else -> locale.getString("inline")
                                        }
                                        EFLog.v("游戏模式: $modeString")

                                        if (mode != 1 || hasUnityDataFile) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable(enabled = !isLoading) {
                                                        EFLog.d("用户点击了游戏包: $packageName")
                                                        coroutineScope.launch {
                                                            try {
                                                                isLoading = true
                                                                initializationError = null
                                                                EFLog.i("开始初始化游戏: $packageName")

                                                                withContext(Dispatchers.IO) {
                                                                    when (mode) {
                                                                        0 -> {
                                                                            EFLog.d("使用外部模式初始化")
                                                                            val gameAbis =
                                                                                Apk.getSupportedAbi(
                                                                                    packageName
                                                                                )
                                                                            EFLog.i("检测到游戏支持的ABI架构: ${gameAbis ?: "未检测到"}")

                                                                            EFMod.initialize(
                                                                                State.EFModPath,
                                                                                State.EFModLoaderPath,
                                                                                File(
                                                                                    Environment.getExternalStorageDirectory(),
                                                                                    "Documents/TEFModLoader"
                                                                                ).path,
                                                                                if (State.architecture.value == 0) gameAbis else null
                                                                            )
                                                                            EFLog.d("初始化主模块完成")

                                                                            EFMod.initialize_data(
                                                                                State.EFModPath,
                                                                                File(
                                                                                    Environment.getExternalStorageDirectory(),
                                                                                    "Documents/TEFModLoader/Data"
                                                                                ).path
                                                                            )
                                                                            EFLog.d("初始化数据模块完成")

                                                                            configuration.setBoolean(
                                                                                "externalMode",
                                                                                true
                                                                            )
                                                                            EFLog.v("设置外部模式配置为true")


                                                                            EFLog.i("准备启动游戏: $packageName")
                                                                            Apk.launchAppByPackageName(
                                                                                packageName
                                                                            )
                                                                            EFLog.i("游戏启动成功，关闭当前Activity")
                                                                            MainActivity.getContext()
                                                                                .finishAffinity()
                                                                        }
                                                                    }
                                                                }
                                                            } catch (e: Exception) {
                                                                val errorMsg =
                                                                    locale.getString("initialization_failed") + ": ${e.localizedMessage}"
                                                                EFLog.e("初始化失败: $errorMsg", e)
                                                                initializationError = errorMsg
                                                                isLoading = false
                                                            }
                                                        }
                                                    }
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                icon?.let {
                                                    Image(
                                                        bitmap = it.toBitmap().asImageBitmap(),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(60.dp)
                                                            .padding(end = 16.dp)
                                                    )
                                                }

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = packageName,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "${locale.getString("mode")} $modeString",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        text = "${locale.getString("version")} $versionInfo",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = { },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                if (!isLoading) {
                                    EFLog.d("用户点击了取消按钮")
                                    showLaunchDialog = false
                                }
                            },
                            enabled = !isLoading
                        ) {
                            Text(locale.getString("cancel"))
                        }
                    }
                )
            }

            Box(
                contentAlignment = Alignment.TopStart
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        HomeScreen.stateCard(
                            title = locale.getString("running"),
                            description = locale.getString("running_content"),
                            isActive = true,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (File(MainApplication.getContext().getExternalFilesDir(null), "patch/Game.apk").exists()) {
                                    showExportDialog = true
                                } else {
                                    showPatchDialog = true
                                }
                            }
                        )
                    }

                    item {

                        val content = mutableListOf<HomeScreen.UpdateLogData>()
                        updateLog.getMap().forEach {
                            content.add(HomeScreen.UpdateLogData(
                                versionTitle = it.key,
                                content = it.value
                            ))
                        }

                        HomeScreen.updateLogCard(
                            title = locale.getString("update_log"),
                            confirmButton = locale.getString("close"),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            data = content,
                            onClick = {}
                        )
                    }
                }

                var offsetX by remember { mutableFloatStateOf(0f) }
                var offsetY by remember { mutableFloatStateOf(0f) }

                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Default.VideogameAsset, contentDescription = "Launch the game") },
                    text = { Text(locale.getString("launch_the_game")) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if (Apk.doesAnyAppContainMetadata("TEFModLoader") || hasUnityDataFile) {
                            showLaunchDialog = true
                        } else {
                            if (File(MainApplication.getContext().getExternalFilesDir(null), "patch/game.apk").exists()) showExportDialog = true
                            else showPatchDialog = true
                        }
                    },
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                offsetX.roundToInt(),
                                offsetY.roundToInt()
                            )
                        }
                        .align(Alignment.BottomEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        .padding(20.dp)
                )
            }
        }
    }
}