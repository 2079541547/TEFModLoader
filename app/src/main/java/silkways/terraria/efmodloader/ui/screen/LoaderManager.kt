package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.efmod.LoaderManager
import silkways.terraria.efmodloader.logic.efmod.Loader
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.FileUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File
import java.io.FileOutputStream

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
                                    text = kernel.info.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "by ${kernel.info.author}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // 显示版本号（仅在选中时）
                                if (selectedKernelIndex == index) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "v ${kernel.info.version}",
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
                                    text = kernel.info.introduce,
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
                                    text = kernel.info.github.url,
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
                    text = "${jsonUtils.getString("loader", "delete_dialog", "title")} ${deleteTargetKernel?.info?.name}",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        FileUtils.deleteDirectory(File(deleteTargetKernel!!.filePath))
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
    val file = File(context.getExternalFilesDir(null), "EFModLoader/info.json")
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
    val directory = File(context.getExternalFilesDir(null), "EFModLoader")
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
    val loaders = mutableListOf<Loader>()


    if (directory.exists() && directory.isDirectory) {
        // 遍历主目录中的所有子目录
        for (loaderDir in directory.listFiles { file -> file.isDirectory }) {
            if (loaderDir != null && File(loaderDir, "loader").exists()) {
                // 存在一个符合条件的文件，继续处理
                EFLog.i("${File(loaderDir, "loader")}")
                val Infomap = LoaderManager.parseLoaderInfoToMap(loaderDir.absolutePath)
                val LoaderIcon = BitmapFactory.decodeFile(File(loaderDir, "loader.icon").absolutePath)

                val info = Loader(
                    filePath = loaderDir.absolutePath,
                    info = Loader.LoaderInfo(
                        name = Infomap["name"].toString(),
                        author = Infomap["author"].toString(),
                        version = Infomap["version"].toString(),
                        introduce = (Infomap["introduce"] as Map<*, *>)[LanguageHelper.getLanguage(
                            SPUtils.readInt(Settings.languageKey, 0),
                            context
                        )].toString(),
                        github = Loader.GithubInfo(
                            overview = (Infomap["github"] as Map<*, *>)["overview"].toString(),
                            url = (Infomap["github"] as Map<*, *>)["url"].toString()
                        ),
                        mod = Loader.LoaderDetails(
                             platform = Loader.PlatformSupport(
                                Windows = Loader.PlatformArchitectures(
                                    arm64 = (((Infomap["loader"] as Map<*, *>)["platform"] as Map<*, *>)["Windows"] as Map<*, *>)["arm64"] as Boolean,
                                    arm32 = (((Infomap["loader"] as Map<*, *>)["platform"] as Map<*, *>)["Windows"] as Map<*, *>)["arm32"] as Boolean,
                                    x86_64 = (((Infomap["loader"] as Map<*, *>)["platform"] as Map<*, *>)["Windows"] as Map<*, *>)["x86_64"] as Boolean,
                                    x86 = (((Infomap["loader"] as Map<*, *>)["platform"] as Map<*, *>)["Windows"] as Map<*, *>)["x86"] as Boolean
                                ),
                                Android = Loader.PlatformArchitectures(
                                    arm64 = (((Infomap["loader"] as Map<*, *>)["platform"] as Map<*, *>)["Android"] as Map<*, *>)["arm64"] as Boolean,
                                    arm32 = (((Infomap["loader"] as Map<*, *>)["platform"] as Map<*, *>)["Android"] as Map<*, *>)["arm32"] as Boolean,
                                    x86_64 = (((Infomap["loader"] as Map<*, *>)["platform"] as Map<*, *>)["Android"] as Map<*, *>)["x86_64"] as Boolean,
                                    x86 = (((Infomap["loader"] as Map<*, *>)["platform"] as Map<*, *>)["Android"] as Map<*, *>)["x86"] as Boolean
                                )
                            )
                        )
                    ),
                    icon = LoaderIcon
                )

                if (!File(loaderDir, "loader.icon").exists()) {
                    info.icon =
                        BitmapFactory.decodeStream(context.assets.open("TEFModLoader/未知.png"))
                }
                loaders.add(info)
            }
        }
    }
    return loaders
}



@Composable
fun PreviewKernelManager() {
    val dummyKernels = loadLoaderFromDirectory("${LocalContext.current.getExternalFilesDir(null)}/EFModLoader", LocalContext.current)
    KernelManager(context = LocalContext.current, kernelList = dummyKernels)
}
