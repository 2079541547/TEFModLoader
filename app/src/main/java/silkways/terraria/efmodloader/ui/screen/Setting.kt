package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToDrive
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.DriveFileMoveRtl
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File

@SuppressLint("StaticFieldLeak")
private val jsonUtils = LanguageUtils(
    MainApplication.getContext(),
    LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
    "setting"
)

private var targetDialog =  mutableStateOf(false)
private var ManageDialog = mutableStateOf(false)
private var ImportDialog = mutableStateOf(false)
private var ExportDialog = mutableStateOf(false)


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    var settings by remember { mutableStateOf(buildSettingsList(context)) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            settings = buildSettingsList(context)
        }
    }

    Scaffold(
        topBar = { CustomTopBar(jsonUtils.getString("title")) },
        content = { padding ->
            LazyColumn(contentPadding = padding) {
                items(settings.size) { index ->
                    val setting = settings[index]
                    when (setting) {
                        is SettingItem.Title -> SettingTitle(setting.title)
                        is SettingItem.Button -> SettingButton(setting)
                        is SettingItem.Switch -> SettingSwitch(setting)
                        is SettingItem.PopupMenu -> SettingPopupMenu(setting)
                    }
                }
            }
        }
    )

    if (targetDialog.value) {
        SettingsPacknameDialog(onDismissRequest = { targetDialog.value = false })
    }

    if (ManageDialog.value) {
        SettingsManageDialog(onDismissRequest = { ManageDialog.value = false })
    }

    if (ImportDialog.value) {
        SettingsImportDialog(onDismissRequest = { ImportDialog.value = false })
    }

    if (ExportDialog.value) {
        SettingsExportDialog(onDismissRequest = { ExportDialog.value = false })
    }
}

fun buildSettingsList(context: Context): List<SettingItem> {

    context as Activity
    return listOf(

        SettingItem.Title(jsonUtils.getString("important", "title")),

        SettingItem.PopupMenu(
            icon = Icons.Filled.Api,
            title = jsonUtils.getString("important", "runtime", "title"),
            subtitle =  when (SPUtils.readInt(Settings.Runtime, 0)) {
                0 -> jsonUtils.getString("important", "runtime", "external")
                1 -> jsonUtils.getString("important", "runtime", "share")
                else -> jsonUtils.getString("important", "runtime", "embed")
            },
            menuOptions = listOf(
                jsonUtils.getString("important", "runtime", "external"),
                jsonUtils.getString("important", "runtime", "share"),
                jsonUtils.getString("important", "runtime", "embed")),
            onMenuItemClick = { menuItem ->
                when(menuItem) {
                    jsonUtils.getString("important", "runtime", "external") -> {
                        SPUtils.putInt(Settings.Runtime, 0)
                        SPUtils.putBoolean("ApplyForPermission", true)
                        File("${context.externalCacheDir}/Reboot").mkdirs()
                    }
                    jsonUtils.getString("important", "runtime", "share") -> SPUtils.putInt(Settings.Runtime, 1)
                    jsonUtils.getString("important", "runtime", "embed") -> SPUtils.putInt(Settings.Runtime, 2)
                }
            }
        ),

        SettingItem.PopupMenu(
            icon = Icons.Filled.Architecture,
            title = jsonUtils.getString("important", "architecture", "title"),
            subtitle =  when (SPUtils.readInt(Settings.architecture, 0)) {
                0 -> jsonUtils.getString("important", "architecture", "system")
                1 -> jsonUtils.getString("important", "architecture", "arm64-v8a")
                else -> jsonUtils.getString("important", "architecture", "armeabi-v7a")
            },
            menuOptions = listOf(
                jsonUtils.getString("important", "architecture", "system"),
                jsonUtils.getString("important", "architecture", "arm64-v8a"),
                jsonUtils.getString("important", "architecture", "armeabi-v7a")),
            onMenuItemClick = { menuItem ->
                when(menuItem) {
                    jsonUtils.getString("important", "architecture", "system") -> SPUtils.putInt(Settings.architecture, 0)
                    jsonUtils.getString("important", "architecture", "arm64-v8a") -> SPUtils.putInt(Settings.architecture, 1)
                    jsonUtils.getString("important", "architecture", "armeabi-v7a") -> SPUtils.putInt(Settings.architecture, 2)
                }
            }
        ),

        SettingItem.Button(
            icon = Icons.Filled.AddToDrive,
            title = jsonUtils.getString("important", "target", "title"),
            onClick = {
                targetDialog.value = true
            }
        ),


        SettingItem.Title(jsonUtils.getString("general", "title")),

        SettingItem.PopupMenu(
            icon = Icons.Filled.ColorLens,
            title = jsonUtils.getString("general", "theme", "title"),
            subtitle =  when (SPUtils.readInt(Settings.themeKey, -1)) {
                1 -> jsonUtils.getString("general", "theme", "light")
                2 -> jsonUtils.getString("general", "theme", "dark")
                else -> jsonUtils.getString("general", "theme", "system")
            },
            menuOptions = listOf(
                jsonUtils.getString("general", "theme", "system"),
                jsonUtils.getString("general", "theme", "light"),
                jsonUtils.getString("general", "theme", "dark")
            ),
            onMenuItemClick = { menuItem ->
                when(menuItem){
                    jsonUtils.getString("general", "theme", "system") -> {
                        File("${context.externalCacheDir}/Reboot").mkdirs()
                        SPUtils.putInt(Settings.themeKey, -1)
                        context.recreate()
                    }
                    jsonUtils.getString("general", "theme", "light") -> {
                        File("${context.externalCacheDir}/Reboot").mkdirs()
                        SPUtils.putInt(Settings.themeKey, 1)
                        context.recreate()
                    }
                    jsonUtils.getString("general", "theme", "dark") -> {
                        File("${context.externalCacheDir}/Reboot").mkdirs()
                        SPUtils.putInt(Settings.themeKey, 2)
                        context.recreate()
                    }
                }
            }
        ),

        SettingItem.PopupMenu(
            icon = Icons.Filled.Language,
            title = jsonUtils.getString("general", "language", "title"),
            subtitle = when(SPUtils.readInt(Settings.languageKey, 0)){
                0 -> jsonUtils.getString("general", "language", "system")
                else -> jsonUtils.getString("general", "language", LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()))
            },
            menuOptions = listOf(
                jsonUtils.getString("general", "language", "system"),
                jsonUtils.getString("general", "language", "zh-cn"),
                jsonUtils.getString("general", "language", "zh-hant"),
                jsonUtils.getString("general", "language", "ru"),
                jsonUtils.getString("general", "language", "en"),
                jsonUtils.getString("general", "language", "jp"),
                jsonUtils.getString("general", "language", "ko"),
                jsonUtils.getString("general", "language", "it"),
                jsonUtils.getString("general", "language", "es"),
                jsonUtils.getString("general", "language", "fr"),
                jsonUtils.getString("general", "language", "de")
            ),
            onMenuItemClick = { menuItem ->
                val index = listOf(
                    jsonUtils.getString("general", "language", "system"),
                    jsonUtils.getString("general", "language", "zh-cn"),
                    jsonUtils.getString("general", "language", "zh-hant"),
                    jsonUtils.getString("general", "language", "ru"),
                    jsonUtils.getString("general", "language", "en"),
                    jsonUtils.getString("general", "language", "jp"),
                    jsonUtils.getString("general", "language", "ko"),
                    jsonUtils.getString("general", "language", "it"),
                    jsonUtils.getString("general", "language", "es"),
                    jsonUtils.getString("general", "language", "fr"),
                    jsonUtils.getString("general", "language", "de")
                ).indexOf(menuItem)
                if (index != -1) {
                    setLanguage(index)
                    File("${context.externalCacheDir}/Reboot").mkdirs()
                    context.recreate()
                } else {
                    EFLog.e("未找到匹配的语言选项")
                }
            }
        ),

        SettingItem.Title(jsonUtils.getString("file", "title")),

        SettingItem.Button(
            icon = Icons.Filled.FolderOpen,
            title = jsonUtils.getString("file", "manage", "title"),
            onClick = {
                ManageDialog.value = true
            }
        ),

        SettingItem.Button(
            icon = Icons.Filled.DriveFileMove,
            title = jsonUtils.getString("file", "import", "title"),
            onClick = {
                ImportDialog.value = true
            }
        ),

        SettingItem.Button(
            icon = Icons.Filled.DriveFileMoveRtl,
            title = jsonUtils.getString("file", "export", "title"),
            onClick = {
                ExportDialog.value = true
            }
        ),

        SettingItem.Title(jsonUtils.getString("log", "title")),

        SettingItem.Switch(
            icon = Icons.Filled.AssignmentLate,
            title = jsonUtils.getString("log", "switch"),
            isChecked = SPUtils.readBoolean("LogCache", true),
            onCheckedChange = { it ->
                SPUtils.putBoolean("LogCache", it)
            }
        ),

        SettingItem.PopupMenu(
            icon = Icons.Filled.Construction,
            title = jsonUtils.getString("log", "cache", "title"),
            subtitle = when(SPUtils.readInt("LogCacheSize", 0)){
                1024 -> jsonUtils.getString("log", "cache", "1024")
                4096 -> jsonUtils.getString("log", "cache", "4096")
                8192 -> jsonUtils.getString("log", "cache", "8192")
                else -> jsonUtils.getString("log", "cache", "unlimited")
            },
            menuOptions = listOf(
                jsonUtils.getString("log", "cache", "1024"),
                jsonUtils.getString("log", "cache", "4096"),
                jsonUtils.getString("log", "cache", "8192"),
                jsonUtils.getString("log", "cache", "unlimited"),
                ),
            onMenuItemClick = { menuItem ->
                when(menuItem) {
                    jsonUtils.getString("log", "cache", "1024") -> SPUtils.putInt("LogCacheSize", 1024)
                    jsonUtils.getString("log", "cache", "4096") -> SPUtils.putInt("LogCacheSize", 4096)
                    jsonUtils.getString("log", "cache", "8192") -> SPUtils.putInt("LogCacheSize", 8192)
                    jsonUtils.getString("log", "cache", "unlimited") -> SPUtils.putInt("LogCacheSize", 0)
                }
            }
        )
    )
}

private fun setLanguage(code: Int) {
    SPUtils.putInt(Settings.languageKey, code)
    LanguageUtils(
        MainApplication.getContext(),
        LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
        ""
    ).loadJsonFromAsset()
}


@Composable
fun SettingTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SettingButton(setting: SettingItem.Button) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { setting.onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = setting.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = setting.title,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
fun SettingSwitch(setting: SettingItem.Switch) {
    var isChecked by remember { mutableStateOf(setting.isChecked) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = setting.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = setting.title,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            Switch(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = it
                    setting.onCheckedChange(it)
                }
            )
        }
    }
}

@Composable
fun SettingPopupMenu(setting: SettingItem.PopupMenu) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { expanded = true },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = setting.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = setting.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = setting.subtitle,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            setting.menuOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        setting.onMenuItemClick(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun SettingsPacknameDialog(onDismissRequest: () -> Unit) {
    var packageName by remember { mutableStateOf(SPUtils.readString(Settings.GamePackageName, "com.and.games505.TerrariaPaid").toString()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = jsonUtils.getString("important", "target", "dialog", "title")) },
        text = {
            OutlinedTextField(
                value = packageName,
                onValueChange = { packageName = it },
                label = { Text(text = jsonUtils.getString("important", "target", "dialog", "hint")) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    SPUtils.putString(Settings.GamePackageName, packageName)
                    onDismissRequest()
                }
            ) {
                Text(text = jsonUtils.getString("important", "target", "dialog", "determine"))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = jsonUtils.getString("important", "target", "dialog", "cancel"))
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}


@Composable
fun SettingsManageDialog(onDismissRequest: () -> Unit) {
    var managePath by remember { mutableStateOf(SPUtils.readString(Settings.FileManagementPath, "${MainApplication.getContext().getExternalFilesDir(null)}").toString()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = jsonUtils.getString("file", "manage", "dialog", "title")) },
        text = {
            OutlinedTextField(
                value = managePath,
                onValueChange = { managePath = it },
                label = { Text(text = jsonUtils.getString("file", "manage", "dialog", "hint")) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    SPUtils.putString(Settings.FileManagementPath, managePath)
                    onDismissRequest()
                }
            ) {
                Text(text = jsonUtils.getString("file", "manage", "dialog", "determine"))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = jsonUtils.getString("file", "manage", "dialog", "cancel"))
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}


@Composable
fun SettingsImportDialog(onDismissRequest: () -> Unit) {
    var importPath by remember { mutableStateOf(SPUtils.readString(Settings.FileImportPath, "${MainApplication.getContext().getExternalFilesDir(null)}").toString()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = jsonUtils.getString("file", "import", "dialog", "title")) },
        text = {
            OutlinedTextField(
                value = importPath,
                onValueChange = { importPath = it },
                label = { Text(text = jsonUtils.getString("file", "import", "dialog", "hint")) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    SPUtils.putString(Settings.FileImportPath, importPath)
                    onDismissRequest()
                }
            ) {
                Text(text = jsonUtils.getString("file", "import", "dialog", "determine"))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = jsonUtils.getString("file", "import", "dialog", "cancel"))
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
fun SettingsExportDialog(onDismissRequest: () -> Unit) {
    var exportPath by remember { mutableStateOf(SPUtils.readString(Settings.FileExportPath, "${MainApplication.getContext().getExternalFilesDir(null)}").toString()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = jsonUtils.getString("file", "export", "dialog", "title")) },
        text = {
            OutlinedTextField(
                value = exportPath,
                onValueChange = { exportPath = it },
                label = { Text(text = jsonUtils.getString("file", "export", "dialog", "hint")) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    SPUtils.putString(Settings.FileExportPath, exportPath)
                    onDismissRequest()
                }
            ) {
                Text(text = jsonUtils.getString("file", "export", "dialog", "determine"))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = jsonUtils.getString("file", "export", "dialog", "cancel"))
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

sealed class SettingItem {
    data class Title(val title: String) : SettingItem()
    data class Button(
        val icon: ImageVector,
        val title: String,
        val onClick: () -> Unit
    ) : SettingItem()
    data class Switch(
        val icon: ImageVector,
        val title: String,
        val isChecked: Boolean,
        val onCheckedChange: (Boolean) -> Unit
    ) : SettingItem()
    data class PopupMenu(
        val icon: ImageVector,
        val title: String,
        val subtitle: String,
        val menuOptions: List<String>,
        val onMenuItemClick: (String) -> Unit
    ) : SettingItem()
}