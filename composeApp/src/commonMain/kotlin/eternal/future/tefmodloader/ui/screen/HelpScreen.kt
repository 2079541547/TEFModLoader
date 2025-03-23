package eternal.future.tefmodloader.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.utility.App
import eternal.future.tefmodloader.ui.AppTopBar
import eternal.future.tefmodloader.ui.navigation.BackMode
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.utility.Locales

object HelpScreen {

    data class HelpItem(
        val id: Int,
        val question: String,
        val answer: String
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HelpScreen(mainViewModel: NavigationViewModel) {

        val locale = Locales().loadLocalization("Screen/HelpScreen.toml", Locales.getLanguage(State.language.value))

        val helpItems = mutableListOf<HelpItem>()
        var i = 0

        locale.getMap().forEach {
            if (it.key != "title" &&
                it.key != "exit" &&
                it.key != "search") {
                i++
                helpItems.add(
                    HelpItem(
                        id = i,
                        question = it.key,
                        answer = it.value
                    )
                )
            }
        }

        var searchQuery by remember { mutableStateOf("") }
        val filteredItems = if (searchQuery.isEmpty()) {
            helpItems
        } else {
            helpItems.filter {
                it.id.toString().contains(searchQuery, ignoreCase = true) ||
                        it.question.contains(searchQuery, ignoreCase = true) ||
                        it.answer.contains(searchQuery, ignoreCase = true)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    val menuItems = mapOf(locale.getString("exit") to Pair(Icons.AutoMirrored.Filled.ExitToApp) { App.exit() })

                    AppTopBar(
                        title = locale.getString("title"),
                        showMenu = true,
                        menuItems = menuItems,
                        showBackButton = true,
                        onBackClick = {
                            mainViewModel.navigateBack(BackMode.TO_DEFAULT)
                        }
                    )

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(locale.getString("search")) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                items(filteredItems.size) { index ->
                    val item = filteredItems[index]
                    var isExpanded by remember { mutableStateOf(false) }
                    val animatedElevation by animateDpAsState(
                        targetValue = if (isExpanded) 8.dp else 2.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )
                    val animatedBackgroundColor by animateColorAsState(
                        targetValue = MaterialTheme.colorScheme.surface,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                isExpanded = !isExpanded
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
                        colors = CardDefaults.elevatedCardColors(containerColor = animatedBackgroundColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${item.id}: ${item.question}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.weight(1f)
                                )
                                if (isExpanded) {
                                    Icon(
                                        imageVector = Icons.Filled.ExpandLess,
                                        contentDescription = "Collapse",
                                        modifier = Modifier.clickable { isExpanded = false }
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.ExpandMore,
                                        contentDescription = "Expand",
                                        modifier = Modifier.clickable { isExpanded = true }
                                    )
                                }
                            }
                            if (isExpanded || searchQuery.isNotEmpty() && filteredItems.indexOf(item) < filteredItems.size) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = item.answer, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}