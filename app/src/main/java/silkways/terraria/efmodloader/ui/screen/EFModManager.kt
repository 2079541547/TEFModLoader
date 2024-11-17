
/*******************************************************************************
 * 文件名称: LoaderManager
 * 项目名称: TEFModLoader-Compose
 * 创建时间: 2024/11/17 上午11:01
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 描述信息: 本文件为TEFModLoader-Compose项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

package silkways.terraria.efmodloader.ui.screen


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import eternal.future.effsystem.fileSystem.EFMC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.efmod.Mod
import silkways.terraria.efmodloader.logic.efmod.ModManager.remove
import silkways.terraria.efmodloader.ui.activity.WebActivity
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File



@SuppressLint("StaticFieldLeak")
private val jsonUtils = LanguageUtils(
    MainApplication.getContext(),
    LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
    "manager"
)

private var snackbarHostState = SnackbarHostState()
private var scope: CoroutineScope? = null

@Composable
fun EFModManagerScreen() {
    scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CustomTopBar(jsonUtils.getString("mod", "title"))
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        content = { innerPadding ->
            val mods = remember {
                loadModsFromDirectory("${MainApplication.getContext().getExternalFilesDir(null)}/TEFModLoader/EFModData", MainApplication.getContext())
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(mods.size) { index ->
                    val mod = mods[index]
                    ModItem(mod)
                }
            }
        }
    )
}



fun loadModsFromDirectory(directoryPath: String, context: Context): List<Mod> {
    val directory = File(directoryPath)
    val mods = mutableListOf<Mod>()

    if (directory.exists() && directory.isDirectory) {
        for (file in directory.listFiles()) {
            if (file.isFile && file.extension != "json") {

                val Infomap = EFMC.getModInfo(file.absolutePath)
                val ModIcon = EFMC.getModIcon(file.absolutePath)

                val info = Mod(
                    filePath = file.absolutePath,
                    identifier = Infomap["identifier"].toString(),
                    modName = Infomap["modName"].toString(),
                    author = Infomap["author"].toString(),
                    introduce = Infomap[LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), context)].toString(),
                    version = Infomap["version"].toString(),
                    openSource = Infomap["openSource"] as Boolean,
                    openSourceUrl = Infomap["openSourceUrl"].toString(),
                    customizePage = Infomap["customizePage"] as Boolean,
                    isEnabled = JsonConfigModifier.readJsonValue(context, "TEFModLoader/EFModData/info.json", file.absolutePath) as Boolean
                )

                info.icon = BitmapFactory.decodeStream(context.assets.open("TEFModLoader/未知.png"))
                if (ModIcon.size != 0) info.icon = BitmapFactory.decodeByteArray(ModIcon, 0, ModIcon.size)

                mods.add(info)
            }
        }
    }

    return mods
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ModItem(mod: Mod) {
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isEnabled by remember { mutableStateOf(mod.isEnabled) } // 这里使用mod.isEnabled初始化
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.5.dp)
            .clickable(onClick = { showDetailsDialog = true })
            .padding(5.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 显示Mod图标
                    if (mod.icon != null) {
                        Image(
                            painter = rememberAsyncImagePainter(mod.icon),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp) // 增大图标尺寸
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Delete, // 默认图标资源ID
                            contentDescription = "Mod Icon",
                            modifier = Modifier.size(56.dp) // 增大图标尺寸
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // 减少间距

                    // 显示Mod标题、作者和版本号
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mod.modName,
                            fontSize = 18.sp, // 减小字体
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "by ${mod.author}",
                            fontSize = 14.sp, // 减小字体
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "v ${mod.version}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 控制Mod的启用状态和按钮
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End) {
                Switch(
                    checked = isEnabled, // 使用isEnabled变量
                    onCheckedChange = { newValue ->
                        isEnabled = newValue // 更新isEnabled状态
                        JsonConfigModifier.modifyJsonConfig(context, "TEFModLoader/EFModData/info.json", mod.filePath, isEnabled) // 调用修改配置的函数
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(8.dp)) // 添加间距
                Row(horizontalArrangement = Arrangement.End) {
                    if (mod.customizePage) {
                        IconButton(onClick = {
                            // 解压page文件夹到缓存目录
                            val cacheDir = context.cacheDir
                            val modCacheDir = File(cacheDir, "EFMOD_WEB")
                            modCacheDir.mkdirs()

                            EFMC.extractPage(mod.filePath, modCacheDir.absolutePath)

                            // 使用FileProvider获取文件URI
                            val mainHtmlFile = File(modCacheDir, "main.html")
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                mainHtmlFile
                            )
                            val intent = Intent(context, WebActivity::class.java)
                            intent.putExtra("Title", mod.modName)
                            intent.putExtra("isMod", true)
                            intent.putExtra("webUrl", uri.toString())
                            intent.putExtra(
                                "private",
                                "${context.getExternalFilesDir(null)}/TEModLoader/EFModData/EFMod-Private/${mod.identifier}/"
                            )
                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Animation,
                                contentDescription = "Animation Mod",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Mod",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    if (showDetailsDialog) {
        AlertDialog(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 18.dp),
            onDismissRequest = { showDetailsDialog = false },
            title = { Text(text = mod.modName, style = MaterialTheme.typography.headlineSmall) }, // 使用适当标题样式
            text = {
                Scaffold(
                    content = { innerPadding ->
                        LazyColumn(
                            contentPadding = innerPadding,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                if (mod.icon != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(mod.icon),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(192.dp) // 增大图标尺寸
                                    )
                                    Spacer(modifier = Modifier.height(16.dp)) // 减少间距
                                }
                                Text(
                                    text = "${jsonUtils.getString("mod", "info_dialog", "version")}${mod.version}",
                                    fontSize = 16.sp, // 减小字体
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp)) // 减少间距
                                Text(
                                    text = "${jsonUtils.getString("mod", "info_dialog", "author")}${mod.author}",
                                    fontSize = 16.sp, // 减小字体
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp)) // 减少间距
                                Text(
                                    text = "${jsonUtils.getString("mod", "info_dialog", "introduce")}${mod.introduce}",
                                    fontSize = 14.sp, // 减小字体
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (mod.openSource && mod.openSourceUrl != null) {
                                    Spacer(modifier = Modifier.height(8.dp)) // 减少间距
                                    Text(
                                        text = "${jsonUtils.getString("mod", "info_dialog", "url")}${mod.openSourceUrl}",
                                        fontSize = 14.sp, // 减小字体
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.clickable {
                                            // 打开URL
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mod.openSourceUrl))
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text(jsonUtils.getString("mod", "info_dialog", "close"), fontSize = 14.sp)
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


    if (showDeleteDialog) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "${jsonUtils.getString("mod", "delete_dialog", "title")} ${mod.modName} ?",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        remove(context, File(mod.filePath), mod.identifier)
                        showDeleteDialog = false
                        scope?.launch {
                            snackbarHostState.showSnackbar(jsonUtils.getString("mod", "delete_dialog", "result"))
                        }
                    }
                ) {
                    Text(text = jsonUtils.getString("mod", "delete_dialog", "determine"), fontSize = 14.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = jsonUtils.getString("mod", "delete_dialog", "cancel"), fontSize = 14.sp)
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