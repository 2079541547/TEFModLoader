package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import eternal.future.effsystem.fileSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.efmod.LoaderManager.remove
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File
import java.io.FileOutputStream

data class Loader(
    val filePath: String,
    val loaderName: String,
    val author: String,
    val introduce: String,
    val version: String,
    val openSourceUrl: String,
    var icon: Bitmap? = null
)

private var scope: CoroutineScope? = null

@SuppressLint("StaticFieldLeak")
private val jsonUtils = LanguageUtils(
    MainApplication.getContext(),
    LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
    "manager"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun KernelManager(context: Context, kernelList: List<Loader>) {
    var selectedKernelIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteTargetKernel by remember { mutableStateOf<Loader?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    scope  = rememberCoroutineScope()

    // 初始化时加载已选择的内核
    LaunchedEffect(Unit) {
        val savedFilePath = loadSelectedLoaderPath(context)
        selectedKernelIndex = kernelList.indexOfFirst { it.filePath == savedFilePath }
    }

    Scaffold(
        topBar = {
            CustomTopBar(jsonUtils.getString("loader", "title"))
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(kernelList) { index, kernel ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = {
                        if (selectedKernelIndex != index) {
                                saveSelectedLoaderPath(context, kernel.filePath)
                                selectedKernelIndex = index
                        } else {
                                saveSelectedLoaderPath(context, "")
                                selectedKernelIndex = null
                        }
                    },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 显示内核图标
                            if (kernel.icon != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(kernel.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp).clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Default Icon",
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))

                            // 显示内核名称和作者
                            Column {
                                Text(
                                    text = kernel.loaderName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "by ${kernel.author}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // 显示版本号（仅在选中时）
                                if (selectedKernelIndex == index) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "v ${kernel.version}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // 删除按钮
                            IconButton(
                                onClick = {
                                    deleteTargetKernel = kernel
                                    showDeleteDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }

                            // 显示选中状态
                            if (selectedKernelIndex == index) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Unselected"
                                )
                            }
                        }

                        // 展开内核信息
                        AnimatedVisibility(
                            visible = selectedKernelIndex == index,
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 }, // 减少初始偏移量，使动画更轻盈
                                animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing) // 更短的持续时间和更自然的插值器
                            ) + fadeIn(
                                animationSpec = tween(durationMillis = 200) // 持续时间与滑动动画相同，确保同步
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { -it / 2 }, // 调整退出偏移量，保持动画一致性
                                animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
                            ) + fadeOut(
                                animationSpec = tween(durationMillis = 200)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 72.dp, end = 16.dp, bottom = 16.dp)
                            ) {
                                Text(
                                    text = jsonUtils.getString("loader", "introduce"),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = kernel.introduce,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = jsonUtils.getString("loader", "url"),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = kernel.openSourceUrl,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }


                    }
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog && deleteTargetKernel != null) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "${jsonUtils.getString("loader", "delete_dialog", "title")} ${deleteTargetKernel?.loaderName}",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        remove(context, File(deleteTargetKernel!!.filePath))
                        showDeleteDialog = false
                        scope?.launch {
                            snackbarHostState.showSnackbar(jsonUtils.getString("loader", "delete_dialog", "result"))
                        }
                        selectedKernelIndex = null
                        deleteTargetKernel = null
                    }
                ) {
                    Text(text = jsonUtils.getString("loader", "delete_dialog", "determine"), fontSize = 14.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = jsonUtils.getString("loader", "delete_dialog", "cancel"), fontSize = 14.sp)
                }
            },
            shape = MaterialTheme.shapes.extraLarge,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            containerColor = MaterialTheme.colorScheme.surface,
            iconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 加载已选择的内核路径
private fun loadSelectedLoaderPath(context: Context): String {
    val file = File(context.getExternalFilesDir(null), "TEFModLoader/EFModLoaderData/info.json")
    return if (file.exists()) {
        try {
            val jsonString = file.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            jsonObject.getString("selectedLoaderPath")
        } catch (e: Exception) {
            ""
        }
    } else {
        ""
    }
}

// 保存已选择的内核路径
private fun saveSelectedLoaderPath(context: Context, filePath: String) {
    val directory = File(context.getExternalFilesDir(null), "TEFModLoader/EFModLoaderData")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val file = File(directory, "info.json")

    val jsonObject = JSONObject()
    jsonObject.put("selectedLoaderPath", filePath)

    FileOutputStream(file).use { fos ->
        fos.write(jsonObject.toString(4).toByteArray())
    }
}

fun loadLoaderFromDirectory(directoryPath: String, context: Context): List<Loader> {
    val directory = File(directoryPath)
    val mods = mutableListOf<Loader>()

    if (directory.exists() && directory.isDirectory) {
        for (file in directory.listFiles()) {
            if (file.isFile && file.extension != "json") {

                val Infomap = fileSystem.EFML.getLoaderInfo(file.absolutePath)

                val info = Loader(
                    filePath = file.absolutePath,
                    loaderName = Infomap["LoaderName"].toString(),
                    author = Infomap["author"].toString(),
                    introduce = Infomap[LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), context)].toString(),
                    version = Infomap["version"].toString(),
                    openSourceUrl = Infomap["openSourceUrl"].toString(),
                )


                info.icon = BitmapFactory.decodeStream(context.assets.open("TEFModLoader/未知.png"))

                try {
                    val ModIcon = fileSystem.EFML.getLoaderIcon(file.absolutePath)
                    if (ModIcon.size != 0) info.icon = BitmapFactory.decodeByteArray(ModIcon, 0, ModIcon.size)
                } catch (A: Exception) {
                    println(A)
                    info.icon = BitmapFactory.decodeStream(context.assets.open("TEFModLoader/未知.png"))
                }

                mods.add(info)
            }
        }
    }
    return mods
}



// 示例用法
@Preview(showBackground = true)
@Composable
fun PreviewKernelManager() {
    val dummyKernels = loadLoaderFromDirectory("${LocalContext.current.getExternalFilesDir(null)}/TEFModLoader/EFModLoaderData/", LocalContext.current)
    KernelManager(context = LocalContext.current, kernelList = dummyKernels)
}
