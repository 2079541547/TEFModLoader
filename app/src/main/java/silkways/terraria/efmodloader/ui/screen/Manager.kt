package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
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
import silkways.terraria.efmodloader.utils.FileUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File

@SuppressLint("StaticFieldLeak")
private val jsonUtils = LanguageUtils(
    MainApplication.getContext(),
    LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
    "main"
)

private var snackbarHostState = SnackbarHostState()
private var scope: CoroutineScope? = null

@SuppressLint("CoroutineCreationDuringComposition")
@Destination
@Composable
fun ManagerScreen() {
    val context = LocalContext.current

    scope = rememberCoroutineScope()

    // 启动选择文件的活动结果
    val selectModsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        handleSelectedFiles(uris, ModManager::install, "TEFModLoader/EFModData", context)
    }

    val selectLoadersLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        handleSelectedFiles(uris, LoaderManager::install, "TEFModLoader/EFModLoaderData", context)
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
}


private fun handleSelectedFiles(
    uris: List<Uri>,
    installAction: (context: Context, file: File, destination: File) -> Unit,
    destPath: String,
    context: Context,
) {
    if (uris.isEmpty()) {
        EFLog.w("没有选择任何文件。")
        return
    }

    val rootDirectory = "${context.getExternalFilesDir(null)?.absolutePath}/$destPath"
    val destination = File(rootDirectory)

    CoroutineScope(Dispatchers.IO).launch {
        uris.forEach { uri ->
            val filePath = FileUtils.getRealPathFromURI(uri, context)
            filePath?.let {
                val file = File(it)
                EFLog.i("开始安装文件：${file.name}")
                withContext(Dispatchers.Main) {
                    try {
                        installAction(context, file, destination)
                        EFLog.i("文件安装成功：${file.name}")
                        snackbarHostState.showSnackbar(jsonUtils.getString("manager", "installation successful"))
                    } catch (e: Exception) {
                        EFLog.e("文件安装失败：${file.name}，原因：${e.message}")
                        snackbarHostState.showSnackbar(jsonUtils.getString("manager", "installation failed"))
                    }
                }
            }
        }
    }
}