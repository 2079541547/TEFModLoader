package eternal.future.efmodloader.ui.widget.welcome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

object GuideScreen {
    @Composable
    fun AgreementCard(
        title: String,
        agreementText: String,
        checkBoxTitle: String,
        onCheckBoxChange: (Boolean) -> Unit
    ) {
        var checkedState by remember { mutableStateOf(false) }
        val scrollState = rememberLazyListState()

        Scaffold { it ->
            ElevatedCard(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Companion.Bold),
                        modifier = Modifier.Companion.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.Companion
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        item {
                            Text(
                                text = agreementText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        modifier = Modifier.Companion.padding(vertical = 8.dp)
                    )

                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Companion.Transparent),
                        headlineContent = {
                            Text(
                                text = checkBoxTitle,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Companion.Medium)
                            )
                        },
                        leadingContent = {
                            Checkbox(
                                checked = checkedState,
                                onCheckedChange = {
                                    checkedState = it
                                    onCheckBoxChange(it)
                                }
                            )
                        },
                        modifier = Modifier.Companion.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            checkedState = !checkedState
                            onCheckBoxChange(checkedState)
                        }
                    )
                }
            }
        }
    }
}