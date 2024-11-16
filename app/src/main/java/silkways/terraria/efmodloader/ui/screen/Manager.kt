package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.SPUtils


@SuppressLint("StaticFieldLeak")
private val jsonUtils = LanguageUtils(
    MainApplication.getContext(),
    LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
    "main"
)

@Destination
@Composable
fun ManagerScreen() {

    Scaffold(
        topBar = {
            CustomTopBar(jsonUtils.getString("manager", "title"))
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
                            onClick = { /* TODO: Handle click */ },
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
                            onClick = { /* TODO: Handle click */ },
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
                                onClick = { /* TODO: Handle click */ },
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
                                onClick = { /* TODO: Handle click */ },
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