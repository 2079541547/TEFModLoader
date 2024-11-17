package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.ui.activity.WebActivity
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.SPUtils

private var showDialog by mutableStateOf(false)
@SuppressLint("StaticFieldLeak")
private val jsonUtils = LanguageUtils(
    MainApplication.getContext(),
    LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
    "about"
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CustomTopBar(jsonUtils.getString("title"))
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val context = LocalContext.current
                                val inputStream = context.assets.open("TEFModLoader/ic_launcher_round.webp")
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                Image(
                                    painter = rememberAsyncImagePainter(bitmap),
                                    contentDescription = "Info",
                                    modifier = Modifier.size(100.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "TEFModLoader",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = jsonUtils.getString("app"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(listOf(
                        jsonUtils.getString("developers", "title") to Icons.Filled.People,
                        jsonUtils.getString("open source license") to Icons.Filled.Info,
                        jsonUtils.getString("open source repository") to Icons.Filled.AccountBalance,
                        jsonUtils.getString("special thanks") to Icons.Filled.ThumbUp,
                        jsonUtils.getString("future plans") to Icons.Filled.Flag,
                    )) { (item, icon) ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = {
                                        when (item) {
                                            jsonUtils.getString("developers", "title") ->  showDialog = true
                                            jsonUtils.getString("open source license") -> {
                                            val intent = Intent(context, WebActivity::class.java)
                                            intent.putExtra("Title", jsonUtils.getString("open source license"))
                                            intent.putExtra("webUrl", "Home/About/Licence")
                                            ContextCompat.startActivity(context, intent, Bundle())
                                            }

                                            jsonUtils.getString("open source repository") -> {
                                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/2079541547/TEFModLoader"))
                                                ContextCompat.startActivity(context, browserIntent, Bundle())
                                            }
                                            jsonUtils.getString("special thanks") -> {
                                                val intent = Intent(context, WebActivity::class.java)
                                                intent.putExtra("Title", jsonUtils.getString("special thanks"))
                                                intent.putExtra("webUrl", "Home/About/SpecialThanks")
                                                ContextCompat.startActivity(context, intent, Bundle())
                                            }
                                            jsonUtils.getString("future plans") -> {
                                                val intent = Intent(context, WebActivity::class.java)
                                                intent.putExtra("Title", jsonUtils.getString("future plans"))
                                                intent.putExtra("webUrl", "Home/About/FuturePlans")
                                                ContextCompat.startActivity(context, intent, Bundle())
                                            }
                                        }
                                    })
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = item,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    )

    if (showDialog) {
        DeveloperDialog()
    }
}

@Composable
fun DeveloperDialog() {
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text(text = jsonUtils.getString("developers", "title")) },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(shape = CircleShape)
                ) {
                    val inputStream = MainApplication.getContext().assets.open("TEFModLoader/df8f15e0cb93233de3f2a3e599d0c844.jpg") // 从 assets 文件夹中打开图片
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    Image(
                        painter = rememberAsyncImagePainter(bitmap),
                        contentDescription = jsonUtils.getString("developers", "title"),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = jsonUtils.getString("developers", "developer_0"))
            }
        },
        confirmButton = {
            TextButton(onClick = { showDialog = false }) {
                Text(jsonUtils.getString("developers", "close"))
            }
        }
    )
}