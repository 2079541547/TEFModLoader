package eternal.future.efmodloader.ui.screen.main

import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import eternal.future.efmodloader.MainApplication
import eternal.future.efmodloader.State
import eternal.future.efmodloader.State.ApkPath
import eternal.future.efmodloader.State.Debugging
import eternal.future.efmodloader.State.Mode
import eternal.future.efmodloader.State.OverrideVersion
import eternal.future.efmodloader.State.SignatureKiller
import eternal.future.efmodloader.State.gamePack
import eternal.future.efmodloader.configuration
import eternal.future.efmodloader.ui.widget.main.EFModScreen.localesText
import eternal.future.efmodloader.ui.widget.main.HomeScreen
import eternal.future.efmodloader.ui.widget.main.SettingScreen
import eternal.future.efmodloader.utility.Apk
import eternal.future.efmodloader.utility.EFMod
import eternal.future.efmodloader.utility.copyApk
import eternal.future.efmodloader.utility.doesAnyAppContainMetadata
import eternal.future.efmodloader.utility.encoderAXml
import eternal.future.efmodloader.utility.extractWithPackageName
import eternal.future.efmodloader.utility.launchRandomAppWithMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

var pathcing = mutableStateOf(false)


actual object HomeScreen{
    @Composable
    actual fun HomeScreen() {

        configuration.setBoolean("patched", File(MainApplication.getContext().getExternalFilesDir(null), "patch/Game.apk").exists())
        if (State.patched.value != configuration.getBoolean("patched", false)) {
            State.patched.value = configuration.getBoolean("patched", false)
        }

        val selectFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { url ->
            url?.let {
                ApkPath.value = it.toString()
            }
        }

        var showPatchDialog by remember { mutableStateOf(false) }

        val exportFileLauncher = rememberLauncherForActivityResult(CreateDocument("application/vnd.android.package-archive")) { uri: Uri? ->
            uri?.let {
                MainApplication.getContext().contentResolver.openOutputStream(it).use { outputStream ->
                    val file = File(MainApplication.getContext().getExternalFilesDir(null), "patch/Game.apk")
                    outputStream?.write(file.readBytes())
                    file.delete()
                    configuration.setBoolean("patched", File(MainApplication.getContext().getExternalFilesDir(null), "patch/Game.apk").exists())
                    if (State.patched.value != configuration.getBoolean("patched", false)) {
                        State.patched.value = configuration.getBoolean("patched", false)
                    }
                }
            }
        }

        if (pathcing.value) {
            patch()
        }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { a -> a
            Box(
                contentAlignment = Alignment.TopStart
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        if (!State.patched.value && !Apk.doesAnyAppContainMetadata("TEFModLoader")) {
                            HomeScreen.stateCard(
                                title = "未修补游戏",
                                description = "如果你不修补并安装游戏你将无法享受Mod，点击此卡片使用引导配置进行修补，长按卡片重新配置修补",
                                isActive = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                showPatchDialog = true
                                            }
                                        )
                                    },
                                onClick = {
                                    pathcing.value = true
                                }
                            )
                        } else if (!Apk.doesAnyAppContainMetadata("TEFModLoader")) {
                            HomeScreen.stateCard(
                                title = "未安装修补的安装包",
                                description = "你已修补但是没有安装修补包，请点击此卡片导出安装包并使用MT管理器进行签名，长按则重新配置并修补",
                                isActive = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                showPatchDialog = true
                                            }
                                        )
                                    },
                                onClick = {
                                    when(Mode.value) {
                                        0 -> EFMod.initialize(State.EFModPath, State.EFModLoaderPath, File(
                                            Environment.getExternalStorageDirectory(),
                                            "Documents/TEFModLoader").path)
                                    }
                                    exportFileLauncher.launch("Terraria-Patch.apk")
                                }
                            )
                        } else if (Apk.doesAnyAppContainMetadata("TEFModLoader")){
                            HomeScreen.stateCard(
                                title = "已激活",
                                description = "这里还没想好写什么",
                                isActive = true,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {}
                            )
                        } else {
                            HomeScreen.stateCard(
                                title = "被玩坏捏",
                                description = "无法检测出当前状态，请尝试清除数据",
                                isActive = false,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {}
                            )
                        }
                    }

                    item {
                        HomeScreen.updateLogCard(
                            title = "Changelog",
                            confirmButton = "close",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            data = listOf(
                                HomeScreen.UpdateLogData(
                                    versionTitle = "v1.0.0.0",
                                    content = "Updated content"
                                )
                            ),
                            onClick = {}
                        )
                    }
                }

                if (Apk.doesAnyAppContainMetadata("TEFModLoader")) {
                    val fabXOffset: Dp by animateDpAsState(
                        targetValue = 0.dp,
                        animationSpec = tween(durationMillis = 300)
                    )
                    var dragOffset by remember { mutableStateOf(0f) }

                    ExtendedFloatingActionButton(
                        text = { Text("Launch the game") },
                        icon = {
                            Icon(
                                Icons.Default.Games,
                                contentDescription = "启动游戏"
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            when (Mode.value) {
                                0 -> {
                                    EFMod.initialize(
                                        State.EFModPath, State.EFModLoaderPath,
                                        File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader").path
                                    )
                                }
                            }
                            Apk.launchRandomAppWithMetadata("TEFModLoader")
                                  },
                        modifier = Modifier
                            .offset(x = with(LocalDensity.current) { (fabXOffset.value + dragOffset).toDp() })
                            .align(Alignment.BottomEnd)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    dragOffset += dragAmount.x
                                    change.consume()
                                }
                            }
                            .graphicsLayer(
                                translationX = dragOffset
                            )
                            .padding(20.dp)
                    )
                }
            }
        }

        if (showPatchDialog) {
            AlertDialog(
                onDismissRequest = {  showPatchDialog = false  },
                title = { Text("修补菜单") },
                text = {
                    LazyColumn {
                        item {
                            val ModeMap = mapOf(
                                0 to "外部模式",
                                // 1 to "Share",
                                // 2 to "Inline",
                                // 3 to "Root(risky)",
                            )

                            SettingScreen.Selector(
                                title = "Select Mode",
                                defaultSelectorId = SignatureKiller.value,
                                ModeMap,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                onClick = { select ->
                                    Mode.value = select
                                    configuration.setInt("Mode", select)
                                }
                            )

                            SettingScreen.ModernCheckBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                title = "覆写版本代码",
                                contentDescription = "方便降级安装",
                                isChecked = OverrideVersion.value,
                                onCheckedChange = { select ->
                                    OverrideVersion.value = select
                                }
                            )


                            SettingScreen.ModernCheckBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                title = "调试",
                                contentDescription = "允许被调试",
                                isChecked = Debugging.value,
                                onCheckedChange = { select ->
                                    Debugging.value = select
                                }
                            )

                            SettingScreen.ModernCheckBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                title = "共存",
                                contentDescription = "将使用另一个包名",
                                isChecked = gamePack.value,
                                onCheckedChange = { select ->
                                    gamePack.value = select
                                }
                            )
                        }

                        item {
                            SettingScreen.PathInputWithFilePicker(
                                title = "选择一个APK文件(可不选)",
                                path = ApkPath.value.toString(),
                                onPathChange = {},
                                onFolderSelect = {
                                    selectFileLauncher.launch("application/vnd.android.package-archive")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showPatchDialog = false
                        pathcing.value = true
                    }) {
                        Text(localesText.getString("确定"))
                    }
                }
            )
        }

    }
}

@Composable
private fun patch() {
    if (Mode.value != 3) {
        if (!Apk.doesAnyAppContainMetadata("TEFModLoader") && !configuration.getBoolean("patched", false)) {
            var showDialog by remember { mutableStateOf(true) }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { /* 不允许关闭对话框直到修补完成 */ },
                    title = { Text("修补中") },
                    text = {
                        Column {
                            Text("请耐心等待")
                            CircularProgressIndicator()
                        }
                    },
                    confirmButton = {}
                )

                LaunchedEffect(showDialog) {
                    if (showDialog) {
                        withContext(Dispatchers.IO) {
                            var Packname = "";
                            File(MainApplication.getContext().getExternalFilesDir(null), "patch/Game.apk").let {
                                it.parentFile?.mkdirs()
                                if (State.autoPatch.value) {
                                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                        // State.Mode.value = 1
                                        // configuration.setInt("Mode", 1)
                                    }
                                    Packname = "eternal.future"
                                    Apk.extractWithPackageName("com.and.games505.TerrariaPaid", it.path)
                                } else {
                                    if (ApkPath.value.toUri().path != "") {
                                        if (!it.exists()) Apk.copyApk(ApkPath.value, it.path)
                                    } else {
                                        Apk.extractWithPackageName("com.and.games505.TerrariaPaid", it.path)
                                    }
                                    if (gamePack.value) Packname = "eternal.future"
                                }

                                val axml = File(it.parent, "AndroidManifest.xml")
                                val axml_temp = File(it.parent, "AndroidManifest_temp.xml")

                                Apk.extractFileFromApk(it.path, "AndroidManifest.xml", axml.path)

                                Apk.decodeAXml(axml.path, axml_temp.path)
                                Apk.modifyManifest(axml_temp.path,
                                    Debugging.value, OverrideVersion.value, Packname)
                                axml.delete()
                                Apk.encoderAXml(axml_temp.path, axml.path)
                                Apk.replaceFileInApk(it.path, "AndroidManifest.xml", axml.path)

                                val dexc = Apk.countDexFiles(it.path) + 1
                                javaClass.classLoader?.getResourceAsStream("patch/classes.dex")
                                javaClass.classLoader?.getResourceAsStream("patch/config.json")

                                val cj = File(it.parent, "config.json")
                                val d = File(it.parent, "classes$dexc.dex")

                                FileOutputStream(cj).use { fileOutputStream ->
                                    javaClass.classLoader?.getResourceAsStream("patch/config.json")?.copyTo(fileOutputStream)
                                }

                                FileOutputStream(d).use { fileOutputStream ->
                                    javaClass.classLoader?.getResourceAsStream("patch/classes.dex")?.copyTo(fileOutputStream)
                                }

                                Apk.replaceFileInApk(it.path, d.name, d.path)
                                Apk.replaceFileInApk(it.path, "assets/config.json", cj.path)

                                cj.delete()
                                d.delete()

                                axml_temp.delete()
                                axml.delete()
                            }
                        }

                        showDialog = false
                        State.patched.value = true
                        configuration.setBoolean("patched", true)
                        pathcing.value = false
                    }
                }
            }
        }
    }
}