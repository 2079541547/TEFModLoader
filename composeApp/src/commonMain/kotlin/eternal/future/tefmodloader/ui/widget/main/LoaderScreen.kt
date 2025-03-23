package eternal.future.tefmodloader.ui.widget.main

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.utility.Net
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.compose_multiplatform
import eternal.future.tefmodloader.data.EFModLoader
import eternal.future.tefmodloader.data.PlatformSupport
import eternal.future.tefmodloader.utility.Locales
import java.io.File

object LoaderScreen {

    val localesText = Locales()

    init {
        localesText.loadLocalization("Widget/LoaderScreen.toml", Locales.getLanguage(State.language.value))
    }

    @Composable
    fun LoaderCard_Reuse(
        loader: EFModLoader,
        onUpdateModClick: () -> Unit
    ) {

        var expanded by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var enabled by remember { mutableStateOf(loader.isEnabled) }
        var isVisible by remember { mutableStateOf(true) }

        if (isVisible) {
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
                            Image(
                                bitmap = loader.icon,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp).clip(
                                    CircleShape
                                )
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.compose_multiplatform),
                                contentDescription = "Default Icon",
                                modifier = Modifier.size(56.dp)
                            )
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
                            Text(
                                text = "v${loader.info.version}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Checkbox(checked = enabled, onCheckedChange = { check ->
                            val file = File(loader.path, "enabled")
                            if (check) file.mkdirs() else file.delete()
                            enabled = check
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
                                        contentDescription = "Delete Loader",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                                IconButton(onClick = { onUpdateModClick() }) {
                                    Icon(Icons.Default.Update, contentDescription = "Update Loader")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = loader.introduces.description,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            TextButton(onClick = { Net.openUrlInBrowser(loader.github.url) }) {
                                Text("Github")
                            }

                            var isAndroidExpanded by remember { mutableStateOf(false) }
                            ExpandableSection(
                                title = localesText.getString("android_support"),
                                expanded = isAndroidExpanded,
                                onExpandChange = { isAndroidExpanded = it }) {
                                PlatformSupport(loader.platforms.android)
                            }

                            var isWindowsExpanded by remember { mutableStateOf(false) }
                            ExpandableSection(
                                title = localesText.getString("windows_support"),
                                expanded = isWindowsExpanded,
                                onExpandChange = { isWindowsExpanded = it }) {
                                PlatformSupport(loader.platforms.windows)
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(localesText.getString("delete_the_loader")) },
                    text = { Text("${localesText.getString("delete_the_loader_content")} ${loader.info.name}?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            eternal.future.tefmodloader.utility.EFModLoader.remove(loader.path)
                            isVisible = false
                        }
                        ) {
                            Text(localesText.getString("confirm"))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(localesText.getString("cancel"))
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun LoaderCard(loader: EFModLoader) {
        LoaderCard_o(loader)
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
    private fun PlatformSupport(platform: PlatformSupport) {
        Column {
            Text("x86_64: ${platform.x86_64}", modifier = Modifier.padding(vertical = 4.dp))
            Text("x86_32: ${platform.x86}", modifier = Modifier.padding(vertical = 4.dp))
            Text("arm64: ${platform.arm64}", modifier = Modifier.padding(vertical = 4.dp))
            Text("arm32: ${platform.arm32}", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}


@Composable
expect fun LoaderScreen.LoaderCard_o(loader: EFModLoader)