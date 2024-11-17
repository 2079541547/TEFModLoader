package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.DriveFileMoveRtl
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.ramcosta.composedestinations.annotation.Destination
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.efmod.Init
import silkways.terraria.efmodloader.ui.activity.TerminalActivity
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.FileUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


@SuppressLint("StaticFieldLeak")
private val jsonUtils = LanguageUtils(
    MainApplication.getContext(),
    LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
    "main"
)

private var snackbarHostState = SnackbarHostState()

@Destination
@Composable
fun ToolBoxScreen() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val selectFilesLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            val efmodFilePaths = uris.mapNotNull { uri ->
                FileUtils.getRealPathFromURI(uri, context)?.let { path ->
                    File(path).absolutePath
                }
            }

            if (efmodFilePaths.isNotEmpty()) {
                efmodFilePaths.forEach { filePath ->
                    val file = File(filePath)
                    val extension = file.extension

                    val rootDirectory = SPUtils.readString(Settings.FileImportPath, "${context.getExternalFilesDir(null)?.absolutePath?.let { File(it).parent }}")

                    val destinationPlyPath = "$rootDirectory/Players/"
                    val destinationWldPath = "$rootDirectory/Worlds/"
                    val destinationPath = "$rootDirectory/"

                    when (extension) {
                        "plr" -> {
                            // 复制 .plr 文件
                            handleFile(file, destinationPlyPath, "PLR")
                        }
                        "wld" -> {
                            // 复制 .wld 文件
                            handleFile(file, destinationWldPath, "WLD")
                        }
                        else -> {
                            // 复制其他类型的文件
                            handleFile(file, destinationPath, "OTHER")
                        }
                    }
                }
            } else {
                println("No valid files selected.")
            }
        }
    }

    val createFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uris ->
       if (uris != null) {
           val dirPaths = listOf(
               "${context.getExternalFilesDir(null)?.parent}/Worlds",
               "${context.getExternalFilesDir(null)?.parent}/Players",
               "${context.getExternalFilesDir(null)?.parent}/OldSaves"
           )
           saveZipToFile(uris, dirPaths, context)
       }
    }

    val createFileLauncher_1 = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uris ->
        if (uris != null) {
            saveZipToFile(uris, listOf(), context, true)
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(jsonUtils.getString("toolbox", "title"))
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
                            onClick = {
                                val intent = Intent(context, TerminalActivity::class.java)
                                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Terminal, contentDescription = "Terminal")
                                Text(text = jsonUtils.getString("toolbox", "terminal"), fontSize = 16.sp)
                            }
                        }
                        Button(
                            onClick = {
                                Init(context).initialization()
                                showDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Gamepad, contentDescription = "Game Panel")
                                Text(text = jsonUtils.getString("toolbox", "start"), fontSize = 16.sp)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { selectFilesLauncher.launch("*/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.DriveFileMove, contentDescription = "Import Archive")
                                Text(text = jsonUtils.getString("toolbox", "import archive"), fontSize = 16.sp)
                            }
                        }
                        Button(
                            onClick = { selectFilesLauncher.launch("*/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.DriveFileMove, contentDescription = "Import Configuration")
                                Text(text = jsonUtils.getString("toolbox", "import configuration"), fontSize = 16.sp)
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
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = jsonUtils.getString("toolbox", "manager", "title"), fontSize = 16.sp)
                            FileManagerScreen()
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { createFileLauncher.launch("游戏存档.zip") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.DriveFileMoveRtl, contentDescription = "Dump Save")
                                Text(text = jsonUtils.getString("toolbox", "export archive"), fontSize = 16.sp)
                            }
                        }
                        Button(
                            onClick = { createFileLauncher_1.launch("TEFModLoader数据") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.DriveFileMoveRtl, contentDescription = "Dump All")
                                Text(text = jsonUtils.getString("toolbox", "export data"), fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    )

    if (showDialog) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = jsonUtils.getString("toolbox", "dialog", "title"),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = jsonUtils.getString("toolbox", "dialog", "text"),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(text = jsonUtils.getString("toolbox", "dialog", "close"), fontSize = 14.sp)
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

data class FileItem(val name: String, val isDirectory: Boolean, val path: String)



fun getFilesRecursively(directory: File): List<FileItem> {
    return if (directory.exists() && directory.isDirectory) {
        directory.listFiles()?.flatMap { file ->
            if (file.isDirectory) {
                getFilesRecursively(file)
            } else {
                listOf(FileItem(file.name, false, file.path))
            }
        } ?: emptyList()
    } else {
        emptyList()
    }
}


@Composable
fun FileManagerScreen() {
    val context = LocalContext.current
    val directoryPath = SPUtils.readString(Settings.FileManagementPath, "${context.getExternalFilesDir(null)}").toString()

    val fileItems = remember(directoryPath) {
        val directory = File(directoryPath)
        getFilesRecursively(directory)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.TopStart
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(300.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(fileItems) { index, fileItem ->
                FileItemRow(fileItem, context)
            }
        }
    }
}

@Composable
fun FileItemRow(fileItem: FileItem, context: Context) {
    val icon: ImageVector = if (fileItem.isDirectory) {
        Icons.Filled.Folder
    } else {
        Icons.Filled.Description // 或者其他适合文件的图标
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { shareFile(context, fileItem.path) }
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = fileItem.name)
    }
}

fun shareFile(context: Context, fileName: String) {
    val uri = getUriForFile(context, fileName)
    if (uri != null) {
        Log.i("MainActivity", "URI: $uri")

        // 创建 Intent
        val intent = Intent(Intent.ACTION_VIEW) // Use ACTION_VIEW instead of ACTION_EDIT
        intent.type = "application/octet-stream"
        intent.setDataAndType(uri, "application/octet-stream")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        // 启动 Intent
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("MainActivity", "没有应用可以处理此 Intent", e)
        }
    } else {
        Log.e("MainActivity", "无法获取文件的 Uri")
    }
}

private fun getUriForFile(context: Context, fileName: String): Uri? {
    val externalFilesDir = context.getExternalFilesDir(null)
    if (externalFilesDir != null) {
        val file = File(fileName)
        if (file.exists()) {
            Log.i("MainActivity", "文件存在: ${file.absolutePath}")
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } else {
            Log.i("MainActivity", "文件不存在: ${file.absolutePath}")
            return null
        }
    } else {
        Log.i("MainActivity", "无法获取外部文件目录")
        return null
    }
}


private fun saveZipToFile(
    uri: Uri, dirPaths: List<String> = listOf<String>(), context: Context,
    isAll: Boolean = false) {
    try {
        val fileOutputStream = context.contentResolver.openOutputStream(uri)
        fileOutputStream?.use { outputStream ->
            val zipOut = ZipOutputStream(outputStream)

            if (isAll) {
                val dir = File("sdcard/Android/data/${SPUtils.readString(Settings.FileExportPath, context.packageName)}")
                val dir2 = File("data/data${SPUtils.readString(Settings.FileExportPath, context.packageName)}")

                compressDir(dir, "sdcard/", zipOut)
                compressDir(dir2, "data/", zipOut)
            }
            else {
                for (dirPath in dirPaths) {
                    val dir = File(dirPath)
                    if (dir.exists()) {
                        compressDir(dir, dir.name + "/", zipOut)
                    }

                }
            }
            zipOut.close()
        }
    } catch (e: Exception) {
        Log.e("MainActivity", "Error saving ZIP file", e)
    }
}

private fun compressDir(dir: File, parentPath: String, zipOut: ZipOutputStream) {
    val files = dir.listFiles()
    if (files != null) {
        for (file in files) {
            try {
                if (file.isDirectory) {
                    compressDir(file, parentPath + file.name + "/", zipOut)
                } else {
                    val entryName = parentPath + file.name
                    zipOut.putNextEntry(ZipEntry(entryName))
                    val buffer = ByteArray(1024)
                    var length: Int
                    val fis = FileInputStream(file)
                    while (fis.read(buffer).also { length = it } > 0) {
                        zipOut.write(buffer, 0, length)
                    }
                    zipOut.closeEntry()
                    fis.close()
                }
            } catch (e: Exception) {
                // Log the exception and continue to the next file
                println("Failed to compress file: ${file.absolutePath}. Reason: ${e.message}")
                continue
            }
        }
    }
}



private fun handleFile(file: File, destinationPath: String, fileType: String) {
    val fileName = file.name
    val destinationFile = File(destinationPath, fileName)

    // 创建目录（如果不存在）
    val destinationDirectory = destinationFile.parentFile
    if (destinationDirectory != null) {
        if (!destinationDirectory.exists() && !destinationDirectory.mkdirs()) {
            println("Failed to create directory: ${destinationDirectory.absolutePath}")
            return
        }
    }
        // 文件不存在，复制文件
    FileUtils.copyFile(file.absolutePath, destinationFile.absolutePath, true)
    println("Copied $fileType file: ${file.name} to $destinationPath")
}