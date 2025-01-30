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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import silkways.terraria.efmodloader.data.EFMod
import silkways.terraria.efmodloader.utility.Image
import silkways.terraria.efmodloader.utility.Net
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.compose_multiplatform

object EFModScreen {
    @Composable
    fun EFModCard(
        mod: EFMod,
        onEnabledChange: (Boolean, EFMod) -> Unit,
        onRemoveClick: (EFMod) -> Unit,
        onGoModPageClick: (EFMod) -> Unit,
        onUpdateModClick: (EFMod) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var showInfoDialog by remember { mutableStateOf(false) }
        var enabled by remember { mutableStateOf(mod.isEnabled) }

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
                    if (mod.icon != null) {
                        Image.convertToComposeImage(mod.icon!!)
                            ?.let { Image(bitmap = it, contentDescription = null, modifier = Modifier.size(48.dp)) }
                    } else {
                        Icon(painter = painterResource(Res.drawable.compose_multiplatform), contentDescription = "Default Icon", modifier = Modifier.size(48.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mod.info.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "by ${mod.info.author}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = enabled,
                        onCheckedChange = { check ->
                            enabled = check
                            onEnabledChange(check, mod)
                        }
                    )
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
                            if (mod.info.mod.page) {
                                IconButton(onClick = { onGoModPageClick(mod) }) {
                                    Icon(Icons.Default.Animation, contentDescription = "Animation Mod")
                                }
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Mod", tint = MaterialTheme.colorScheme.error)
                            }
                            IconButton(onClick = { onUpdateModClick(mod) }) {
                                Icon(Icons.Default.Update, contentDescription = "Update Mod")
                            }
                            IconButton(onClick = { showInfoDialog = true }) {
                                Icon(Icons.Default.Info, contentDescription = "Details")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = mod.info.introduce,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete the mod") },
                text = { Text("Are you sure you want to remove ${mod.info.name}?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        onRemoveClick(mod)
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

        if (showInfoDialog) {
            AlertDialog(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                onDismissRequest = { showInfoDialog = false },
                title = { Text(text = "Details - ${mod.info.name}") },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                        var isAuthorExpanded by remember { mutableStateOf(false) }
                        ExpandableSection(title = "Author: ${mod.info.author}", expanded = isAuthorExpanded, onExpandChange = { isAuthorExpanded = it }) {
                            if (mod.info.github.overview.isNotEmpty()) {
                                TextButton(onClick = { Net.openUrlInBrowser(mod.info.github.overview) }) {
                                    Text("View Overview")
                                }
                            }
                        }

                        if (mod.info.github.openSource) {
                            var isGithubExpanded by remember { mutableStateOf(false) }
                            ExpandableSection(title = "GitHub: ${mod.info.github.url}", expanded = isGithubExpanded, onExpandChange = { isGithubExpanded = it }) {
                                if (mod.info.github.url.isNotEmpty()) {
                                    TextButton(onClick = { Net.openUrlInBrowser(mod.info.github.url) }) {
                                        Text("Open in Browser")
                                    }
                                }
                            }
                        }

                        var isIntroExpanded by remember { mutableStateOf(false) }
                        ExpandableSection(title = "Version: ${mod.info.version} | Introduce", expanded = isIntroExpanded, onExpandChange = { isIntroExpanded = it }) {
                            Text(mod.info.introduce, modifier = Modifier.padding(vertical = 8.dp))
                        }

                        var isWindowsExpanded by remember { mutableStateOf(false) }
                        ExpandableSection(title = "Windows Support", expanded = isWindowsExpanded, onExpandChange = { isWindowsExpanded = it }) {
                            PlatformSupport(mod.info.mod.platform.Windows)
                        }

                        var isAndroidExpanded by remember { mutableStateOf(false) }
                        ExpandableSection(title = "Android Support", expanded = isAndroidExpanded, onExpandChange = { isAndroidExpanded = it }) {
                            PlatformSupport(mod.info.mod.platform.Android)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("Close")
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
    private fun PlatformSupport(platform: EFMod.PlatformArchitectures) {
        Column {
            Text("x86_64: ${platform.x86_64}", modifier = Modifier.padding(vertical = 4.dp))
            Text("x86_32: ${platform.x86_32}", modifier = Modifier.padding(vertical = 4.dp))
            Text("arm64: ${platform.arm64}", modifier = Modifier.padding(vertical = 4.dp))
            Text("arm32: ${platform.arm32}", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}