package me.jiangnight.tefmodloader_compose.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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


@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar()

        // LazyColumn 用于创建可滚动的内容列表
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
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

                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
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
            }

            item {
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

                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
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

                Spacer(modifier = Modifier.height(20.dp))  // 添加更多的空白以确保可以滚动
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(title = {
        Text(text = stringResource(R.string.home))
    })
}