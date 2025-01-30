package silkways.terraria.efmodloader.ui.widget.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object HomeScreen {
    @Composable
    fun stateCard(
        title: String,
        description: String,
        isActive: Boolean,
        onClick: () -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        val backgroundColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        val vector = if (isActive) Icons.Filled.CheckCircle else Icons.Filled.Warning
        Card(
            modifier = modifier
                .width(300.dp)
                .padding(16.dp),
            onClick = onClick,
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = vector,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = description,
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
    }

    data class UpdateLogData(
        val versionTitle: String,
        val content: String
    )

    @Composable
    fun updateLogCard(
        title: String,
        confirmButton: String,
        modifier: Modifier = Modifier,
        data: List<UpdateLogData>,
        onClick: () -> Unit
    ) {
        if (data.isEmpty()) return

        val showDialog = remember { mutableStateOf(false) }

        ElevatedCard(
            modifier = modifier
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            onClick = {
                showDialog.value = true
                onClick()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface)

                Text(
                    text = data.first().versionTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(10.dp)
                )
                Spacer(modifier = Modifier.height(4.dp).padding(10.dp))
                Text(
                    text = data.first().content,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(title) },
                text = {
                    LazyColumn {
                        item {
                            data.forEachIndexed { index, item ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(text = item.versionTitle, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.content, style = MaterialTheme.typography.bodySmall)
                                }
                                if (index < data.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text(confirmButton)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            )
        }
    }
}