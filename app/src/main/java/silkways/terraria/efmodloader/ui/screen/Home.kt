package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import org.json.JSONArray
import org.json.JSONObject
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.ui.activity.AboutActivity
import silkways.terraria.efmodloader.ui.activity.SettingActivity
import silkways.terraria.efmodloader.ui.activity.WebActivity
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar
import java.util.Random

@SuppressLint("StaticFieldLeak")
private val jsonUtils = LanguageUtils(
    MainApplication.getContext(),
    LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
    "main"
)

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen() {

    val yiYanArray = remember { getQuotesArray() }
    val (currentYiYan, setCurrentYiYan) = remember { mutableStateOf(getRandomQuote(yiYanArray)) }
    val showUpdateLogsDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopBar(jsonUtils.getString("home", "title"))
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
                // 卡片1：欢迎信息
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { setCurrentYiYan(getRandomQuote(yiYanArray)) }, // 绑定点击事件
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text =
                                when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                                    in 5 until 12 -> jsonUtils.getString("home", "morning") // 早上
                                    in 12 until 13 -> jsonUtils.getString("home", "noon") // 中午
                                    in 13 until 18 -> jsonUtils.getString("home", "afternoon") // 下午
                                    in 18 until 22 -> jsonUtils.getString("home", "night") // 晚上
                                    else -> jsonUtils.getString("home", "good night")
                                },
                                fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = currentYiYan?.optString("text", "") ?: "",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "- ${currentYiYan?.optString("source", "")}",
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .fillMaxWidth()
                            )
                            Text(
                                text = jsonUtils.getString("home", "quotes"),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                item {
                    SpacerAndButtonsSection()
                }
                // 更新日志的卡片
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showUpdateLogsDialog.value = true
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = jsonUtils.getString("Update log", "title"),
                                fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "1.5.5 Stable",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = jsonUtils.getArrayString("Update log", "151"),
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    )

    if (showUpdateLogsDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showUpdateLogsDialog.value = false
            },
            title = {
                Text(text = jsonUtils.getString("Update log", "title"))
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    val logItems = listOf(
                        LogItem("1.5.5 Stable", jsonUtils.getArrayString("Update log", "151")),
                        LogItem("1.5.0", jsonUtils.getArrayString("Update log", "150")),
                        LogItem("1.2.1", jsonUtils.getArrayString("Update log", "121")),
                        LogItem("1.2.0", jsonUtils.getArrayString("Update log", "120")),
                        LogItem("1.0.0", jsonUtils.getArrayString("Update log", "100")),
                    )

                    itemsIndexed(logItems) { index, logItem ->
                        LogItemCard(logItem)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showUpdateLogsDialog.value = false
                }) {
                    Text(text = jsonUtils.getString("Update log", "close"))
                }
            }
        )
    }
}

@Composable
fun SpacerAndButtonsSection() {
    Spacer(modifier = Modifier.height(10.dp))
    val context = LocalContext.current

    // 第一行按钮
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = { MainApplication.getContext().startActivity(Intent(MainApplication.getContext(), AboutActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)) },
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Default.People, contentDescription = "About Icon")
            Text(text = jsonUtils.getString("home", "about"))
        }

        Button(
            onClick = { MainApplication.getContext().startActivity(Intent(MainApplication.getContext(),
                SettingActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)) },
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings Icon")
            Text(text = jsonUtils.getString("home", "setting"))
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 第二行按钮
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/2079541547/Terraria-ToolBox/issues"))
                context.startActivity(browserIntent)
            },
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Default.Report, contentDescription = "Feedback Icon")
            Text(text = jsonUtils.getString("home", "feedback"))
        }

        Button(
            onClick = {
                val intent = Intent(context, WebActivity::class.java)
                intent.putExtra("Title", jsonUtils.getString("home", "help"))
                intent.putExtra("webUrl", "Home/Helps")
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            },
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Default.Help, contentDescription = "Help Icon")
            Text(text = jsonUtils.getString("home", "help"))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp) // 标题左侧间距
            )
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    )
}

private fun getQuotesArray(): JSONArray? {
    val jsonString =
        MainApplication.getContext().assets.open("TEFModLoader/quotes.json").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readText()
            }
        }
    return try {
        JSONObject(jsonString).optJSONArray("quotes")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getRandomQuote(yiYanArray: JSONArray?): JSONObject? {
    if (yiYanArray == null || yiYanArray.length() == 0) return null
    val random = Random()
    val index = random.nextInt(yiYanArray.length())
    return yiYanArray.optJSONObject(index)
}


private data class LogItem(
    val title: String,
    val text: String
)



@Composable
private fun LogItemCard(logItem: LogItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = logItem.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = logItem.text,
            fontSize = 13.sp,
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth()
        )
        Divider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}