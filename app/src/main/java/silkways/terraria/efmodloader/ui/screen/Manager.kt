package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.efmod.LoaderManager
import silkways.terraria.efmodloader.logic.efmod.ModManager
import silkways.terraria.efmodloader.ui.activity.EFManagerActivity
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.UUID

@SuppressLint("StaticFieldLeak")
private val jsonUtils = LanguageUtils(
    MainApplication.getContext(),
    LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
    "main"
)

private var snackbarHostState = SnackbarHostState()
private var scope: CoroutineScope? = null
private val showProgressDialog = mutableStateOf(false)


@SuppressLint("CoroutineCreationDuringComposition")
@Destination
@Composable
fun ManagerScreen() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 启动选择文件的活动结果
    val selectModsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        handleSelectedFiles(context, uris, ModManager::install, "EFMod")
    }

    val selectLoadersLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        handleSelectedFiles(context, uris, LoaderManager::install, "EFModLoader")
    }

    Scaffold(
        topBar = {
            CustomTopBar(jsonUtils.getString("manager", "title"))
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp) // 确保内容不会被顶部栏遮挡
                    .padding(horizontal = 10.dp),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { selectModsLauncher.launch("*/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Install EFMod")
                                Text(text = jsonUtils.getString("manager", "efmod", "install"))
                            }
                        }
                        Button(
                            onClick = {
                                val intent = Intent(context, EFManagerActivity::class.java)
                                intent.putExtra("isMod", true)
                                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = "EFMod Manager")
                                Text(text = jsonUtils.getString("manager", "efmod", "manager"))
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(13.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = jsonUtils.getString("manager", "development settings"),
                                fontSize = 16.sp
                            )
                            Button(
                                onClick = { selectLoadersLauncher.launch("*/*") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Download,
                                        contentDescription = "Install Kernel"
                                    )
                                    Text(text = jsonUtils.getString("manager", "efmodloader", "install"))
                                }
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(context, EFManagerActivity::class.java)
                                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("isMod", false)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "Kernel Management"
                                    )
                                    Text(text = jsonUtils.getString("manager", "efmodloader", "manager"))
                                }
                            }
                        }
                    }
                }
            }
        }
    )
    if (showProgressDialog.value) {
        InstallationProgressDialog()
    }
}

fun handleSelectedFiles(
    context: Context,
    uris: List<Uri>,
    installAction: (inputPath: String, outPath: String) -> Unit,
    dest: String
) {
    // 生成随机目录名
    val randomDirectoryName = UUID.randomUUID().toString()
    val destination = File(context.getExternalFilesDir(null), "$dest/$randomDirectoryName")

    // 确保目标目录存在
    if (!destination.exists()) {
        destination.mkdirs()
    }

    CoroutineScope(Dispatchers.Main).launch {

        if (uris.isEmpty()) {
            EFLog.w("没有选择任何文件。")
            return@launch
        }

        showProgressDialog.value = true

        withContext(Dispatchers.IO) {
            uris.forEachIndexed { index, uri ->
                try {
                    // 创建临时文件用于复制内容
                    val tempFile = File.createTempFile("temp", null, context.cacheDir)
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        FileOutputStream(tempFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    EFLog.i("开始安装文件：${uri.lastPathSegment}")

                    try {
                        installAction(tempFile.absolutePath, destination.absolutePath)
                        EFLog.i("文件安装成功：${uri.lastPathSegment}")
                    } catch (e: Exception) {
                        EFLog.e("文件安装失败：${uri.lastPathSegment}，原因：${e.message}")
                    } finally {
                        // 删除临时文件
                        tempFile.delete()
                    }
                } catch (e: FileNotFoundException) {
                    EFLog.e("无法打开文件流：${uri.lastPathSegment}，原因：${e.message}")
                } finally {
                    // 在最后一个文件处理完毕后关闭进度对话框
                    if (index == uris.lastIndex) {
                        showProgressDialog.value = false
                    }
                }
            }
        }
    }
}


@Composable
fun InstallationProgressDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(jsonUtils.getString("manager", "install", "title")) },
        text = { Text(jsonUtils.getString("manager", "install", "installing")) },
        confirmButton = {}
    )
}