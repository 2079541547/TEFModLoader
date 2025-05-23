package eternal.future.tefmodloader.ui.screen.main

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
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
import eternal.future.tefmodloader.utility.EFMod
import eternal.future.tefmodloader.utility.Locales
import eternal.future.tefmodloader.utility.copyApk
import eternal.future.tefmodloader.utility.doesAnyAppContainMetadata
import eternal.future.tefmodloader.utility.extractWithPackageName
import eternal.future.tefmodloader.utility.getPackageNamesWithMetadata
import eternal.future.tefmodloader.utility.launchAppByPackageName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import kotlin.math.roundToInt

actual object HomeScreen{
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

            if (showLaunchDialog) {
                AlertDialog(
                    onDismissRequest = { showLaunchDialog = false },
                    title = { Text(locale.getString("launch_the_menu"), style = MaterialTheme.typography.headlineSmall) },
                    text = {
                        LazyColumn {
                            val game = Apk.getPackageNamesWithMetadata("TEFModLoader")

                            item {
                                game.forEach { (packageName, mode) ->
                                    val icon: Drawable? = try {
                                        MainApplication.getContext().packageManager.getApplicationIcon(packageName)
                                    } catch (e: PackageManager.NameNotFoundException) {
                                        null
                                    }

                                    val versionInfo = LocalContext.current.packageManager.getPackageInfo(
                                        packageName,
                                        PackageManager.GET_META_DATA
                                    ).versionName ?: locale.getString("unknown_version")

                                    val modeString = when (mode) {
                                        0 -> locale.getString("external")
                                        // 1 -> locale.getString("share")
                                        else -> locale.getString("inline")
                                    }

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {

                                            when(mode) {
                                                0 -> {
                                                    EFMod.initialize(
                                                        State.EFModPath, State.EFModLoaderPath,
                                                        File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader").path
                                                    )
                                                    EFMod.initialize_data(State.EFModPath, File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader/Data").path)
                                                    configuration.setBoolean("externalMode", true)
                                                }
                                            }

                                            Apk.launchAppByPackageName(packageName)
                                            MainActivity.getContext().finishAffinity()
                                        }
                                        .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically) {

                                        Image(
                                            bitmap = icon?.toBitmap()!!.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .padding(end = 16.dp)
                                        )

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
                    },
                    confirmButton = {  },
                    dismissButton = {
                        TextButton(onClick = { showLaunchDialog = false }) {
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
                        if (Apk.doesAnyAppContainMetadata("TEFModLoader")) {
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