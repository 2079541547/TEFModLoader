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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.efmod.ModManager
import silkways.terraria.efmodloader.logic.efmod.Mod
import silkways.terraria.efmodloader.logic.efmod.Mod.GithubInfo
import silkways.terraria.efmodloader.logic.efmod.Mod.ModDetails
import silkways.terraria.efmodloader.logic.efmod.Mod.ModInfo
import silkways.terraria.efmodloader.logic.efmod.Mod.PlatformArchitectures
import silkways.terraria.efmodloader.logic.efmod.Mod.PlatformSupport
import silkways.terraria.efmodloader.ui.activity.ModPage
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.FileUtils
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
                loadModsFromDirectory("${MainApplication.getContext().getExternalFilesDir(null)}/EFMod", MainApplication.getContext())
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
        // 遍历主目录中的所有子目录
        for (modDir in directory.listFiles { file -> file.isDirectory }) {
            if (modDir != null && File(modDir, "mod").exists()) {
                // 存在一个符合条件的文件，继续处理
                EFLog.i("${File(modDir, "mod")}")
                val Infomap = ModManager.parseModInfoToMap(modDir.absolutePath)
                val ModIcon = BitmapFactory.decodeFile(File(modDir, "mod.icon").absolutePath)

                val info = Mod(
                    filePath = modDir.absolutePath,
                    isEnabled = File(modDir, "enable").exists(),
                    info = ModInfo(
                        name = Infomap["name"].toString(),
                        author = Infomap["author"].toString(),
                        version = Infomap["version"].toString(),
                        introduce = (Infomap["introduce"] as Map<*, *>)[LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), context)].toString(),
                        github = GithubInfo(
                            openSource = (Infomap["github"] as Map<*, *>)["open source"] as Boolean,
                            overview = (Infomap["github"] as Map<*, *>)["overview"].toString(),
                            url = (Infomap["github"] as Map<*, *>)["url"].toString()
                        ),
                        mod = ModDetails(
                            Modx = (Infomap["mod"] as Map<*, *>)["Modx"] as Boolean,
                            privateData = (Infomap["mod"] as Map<*, *>)["private data"] as Boolean,
                            page = (Infomap["mod"] as Map<*, *>)["page"] as Boolean,
                            platform = PlatformSupport(
                                Windows = PlatformArchitectures(
                                    arm64 = (((Infomap["mod"] as Map<*, *>)["platform"] as Map<*, *>)["Windows"] as Map<*, *>)["arm64"] as Boolean,
                                    arm32 = (((Infomap["mod"] as Map<*, *>)["platform"] as Map<*, *>)["Windows"] as Map<*, *>)["arm32"] as Boolean,
                                    x86_64 = (((Infomap["mod"] as Map<*, *>)["platform"] as Map<*, *>)["Windows"] as Map<*, *>)["x86_64"] as Boolean,
                                    x86 = (((Infomap["mod"] as Map<*, *>)["platform"] as Map<*, *>)["Windows"] as Map<*, *>)["x86"] as Boolean
                                ),
                                Android = PlatformArchitectures(
                                    arm64 = (((Infomap["mod"] as Map<*, *>)["platform"] as Map<*, *>)["Android"] as Map<*, *>)["arm64"] as Boolean,
                                    arm32 = (((Infomap["mod"] as Map<*, *>)["platform"] as Map<*, *>)["Android"] as Map<*, *>)["arm32"] as Boolean,
                                    x86_64 = (((Infomap["mod"] as Map<*, *>)["platform"] as Map<*, *>)["Android"] as Map<*, *>)["x86_64"] as Boolean,
                                    x86 = (((Infomap["mod"] as Map<*, *>)["platform"] as Map<*, *>)["Android"] as Map<*, *>)["x86"] as Boolean
                                )
                            )
                        )
                    ),
                    icon = ModIcon
                )

                if (!File(modDir, "mod.icon").exists()) {
                    info.icon =
                        BitmapFactory.decodeStream(context.assets.open("TEFModLoader/未知.png"))
                }
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
                            modifier = Modifier.size(56.dp)
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
                            text = mod.info.name,
                            fontSize = 18.sp, // 减小字体
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "by ${mod.info.author}",
                            fontSize = 14.sp, // 减小字体
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "v ${mod.info.version}",
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
                        if (newValue) {
                            File(mod.filePath, "enable").mkdirs()
                        } else {
                            FileUtils.deleteDirectory(File(mod.filePath, "enable"))
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp)) // 添加间距
                Row(horizontalArrangement = Arrangement.End) {
                    if (mod.info.mod.page) {
                        IconButton(onClick = {

                            val intent = Intent(context, ModPage::class.java)
                            intent.putExtra("page", File(mod.filePath, "page.jar").absolutePath)
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
            title = { Text(text = mod.info.name, style = MaterialTheme.typography.headlineSmall) }, // 使用适当标题样式
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
                                            .size(192.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp)) // 减少间距
                                }
                                Text(
                                    text = "${jsonUtils.getString("mod", "info_dialog", "version")}${mod.info.version}",
                                    fontSize = 16.sp, // 减小字体
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp)) // 减少间距
                                Text(
                                    text = "${jsonUtils.getString("mod", "info_dialog", "author")}${mod.info.author}",
                                    fontSize = 16.sp, // 减小字体
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp)) // 减少间距
                                Text(
                                    text = "${jsonUtils.getString("mod", "info_dialog", "introduce")}${mod.info.introduce}",
                                    fontSize = 14.sp, // 减小字体
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (mod.info.github.openSource) {
                                    Spacer(modifier = Modifier.height(8.dp)) // 减少间距
                                    Text(
                                        text = "${jsonUtils.getString("mod", "info_dialog", "url")}${mod.info.github.url}",
                                        fontSize = 14.sp, // 减小字体
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.clickable {
                                            // 打开URL
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mod.info.github.url))
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
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }


    if (showDeleteDialog) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "${jsonUtils.getString("mod", "delete_dialog", "title")} ${mod.info.name} ?",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        FileUtils.deleteDirectory(File(mod.filePath))
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