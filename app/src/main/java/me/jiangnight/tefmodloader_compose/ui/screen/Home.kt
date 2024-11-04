package me.jiangnight.tefmodloader_compose.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import me.jiangnight.tefmodloader_compose.R


@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            CustomTopBar("首页")
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
                        modifier = Modifier.fillMaxWidth(),
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
                                text = stringResource(id = R.string.greetings_1),
                                fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "YiYan",
                                modifier = Modifier
                                    .padding(start = 10.dp, end = 5.dp)
                                    .fillMaxWidth()
                            )
                            Text(
                                text = stringResource(id = R.string.yiyan_text),
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
                            .fillMaxWidth(),
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
                                text = stringResource(id = R.string.logs),
                                fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = stringResource(id = R.string.logs_title_150),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = stringResource(id = R.string.logs_text_150),
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }

                item {

                }
            }
        }
    )
}

@Composable
fun SpacerAndButtonsSection() {

    Spacer(modifier = Modifier.height(10.dp))

    // 第一行按钮
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = { /* TODO: About action */ },
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 10.dp)
        ) {
            Icon(Icons.Default.People, contentDescription = "About Icon")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.about))
        }

        Button(
            onClick = { /* TODO: Settings action */ },
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 10.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings Icon")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.settings))
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 第二行按钮
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = { /* TODO: Feedback action */ },
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 10.dp)
        ) {
            Icon(Icons.Default.Report, contentDescription = "Feedback Icon")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.feedback))
        }

        Button(
            onClick = { /* TODO: Help action */ },
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 10.dp)
        ) {
            Icon(Icons.Default.Help, contentDescription = "Help Icon")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.help))
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