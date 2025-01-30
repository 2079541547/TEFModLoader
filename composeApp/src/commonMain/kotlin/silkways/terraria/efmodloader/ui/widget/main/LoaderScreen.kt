package silkways.terraria.efmodloader.ui.widget.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import silkways.terraria.efmodloader.utility.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import silkways.terraria.efmodloader.utility.Net
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.compose_multiplatform
import silkways.terraria.efmodloader.data.EFModLoader

object LoaderScreen {
    @Composable
    fun LoaderCard(
        loader: EFModLoader,
        onEnabledChange: (Boolean, EFModLoader) -> Unit,
        onRemoveClick: (EFModLoader) -> Unit,
        onUpdateModClick: (EFModLoader) -> Unit) {

        var expanded by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var enabled by remember { mutableStateOf(loader.isSelected) }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { expanded = !expanded },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    if (loader.icon != null) {
                        Image.convertToComposeImage(loader.icon!!)?.let {
                            Image(bitmap = it, contentDescription = null, modifier = Modifier.size(56.dp).clip(
                                CircleShape
                            ))
                        }
                    } else {
                        Icon(painter = painterResource(Res.drawable.compose_multiplatform), contentDescription = "Default Icon", modifier = Modifier.size(56.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = loader.info.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "by ${loader.info.author}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Checkbox(checked = enabled, onCheckedChange = { check ->
                        enabled = check
                        onEnabledChange(check, loader)
                    })
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                    exit = shrinkVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Mod",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            IconButton(onClick = { onUpdateModClick(loader) }) {
                                Icon(Icons.Default.Update, contentDescription = "Update Loader")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = loader.info.introduce,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        TextButton(onClick = { Net.openUrlInBrowser(loader.info.github.url) }) {
                            Text("Github")
                        }

                        var isWindowsExpanded by remember { mutableStateOf(false) }
                        ExpandableSection(
                            title = "Windows Support",
                            expanded = isWindowsExpanded,
                            onExpandChange = { isWindowsExpanded = it }) {
                            PlatformSupport(loader.info.loader.platform.Windows)
                        }

                        var isAndroidExpanded by remember { mutableStateOf(false) }
                        ExpandableSection(
                            title = "Android Support",
                            expanded = isAndroidExpanded,
                            onExpandChange = { isAndroidExpanded = it }) {
                            PlatformSupport(loader.info.loader.platform.Android)
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete the loader") },
                text = { Text("Are you sure you want to remove ${loader.info.name}?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        onRemoveClick(loader)
                    }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    @Composable
    private fun ExpandableSection(title: String, expanded: Boolean, onExpandChange: (Boolean) -> Unit, content: @Composable () -> Unit) {
        Column(modifier = Modifier.clickable { onExpandChange(!expanded) }.fillMaxWidth().padding(horizontal = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(vertical = 4.dp))
                Icon(imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
            ) {
                Box(modifier = Modifier.padding(start = 16.dp)) {
                    content()
                }
            }
        }
    }

    @Composable
    private fun PlatformSupport(platform: EFModLoader.PlatformArchitectures) {
        Column {
            Text("x86_64: ${platform.x86_64}", modifier = Modifier.padding(vertical = 4.dp))
            Text("x86_32: ${platform.x86_32}", modifier = Modifier.padding(vertical = 4.dp))
            Text("arm64: ${platform.arm64}", modifier = Modifier.padding(vertical = 4.dp))
            Text("arm32: ${platform.arm32}", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}