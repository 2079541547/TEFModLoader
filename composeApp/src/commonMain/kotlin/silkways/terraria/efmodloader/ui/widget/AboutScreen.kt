package silkways.terraria.efmodloader.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.painterResource
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.compose_multiplatform

object AboutScreen {
    @Composable
    fun AppIconCard(
        modifier: Modifier = Modifier.Companion,
        labelText: String,
        onClick: () -> Unit = {}
    ) {
        ElevatedCard(
            modifier = modifier
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.compose_multiplatform),
                    contentDescription = "App Icon",
                    modifier = Modifier.Companion.size(128.dp)
                )
                Spacer(modifier = Modifier.Companion.height(8.dp))
                Text(
                    text = "TEFModLoader",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Companion.Bold),
                    textAlign = TextAlign.Companion.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = labelText,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Companion.Light),
                    textAlign = TextAlign.Companion.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    @Composable
    fun aboutWidgets(
        modifier: Modifier,
        icon: ImageVector? = null,
        title: String,
        contentDescription: String,
        onClick: () -> Unit
    ) {
        Row(
            modifier = modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.Companion.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.Companion.size(35.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Companion.Light)
                )
                if (contentDescription.isNotEmpty()) {
                    Text(
                        text = contentDescription,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Companion.Light),
                    )
                }
            }
        }
    }

    @Composable
    fun expandableWidget(
        modifier: Modifier = Modifier.Companion,
        icon: Painter? = null,
        title: String,
        detailedInfo: String,
        isCircularIcon: Boolean = false,
        onClick: () -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        Column(modifier = modifier.clickable(onClick = {
            onClick()
            expanded = !expanded
        })) {
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Companion.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                icon?.let {
                    Box(
                        modifier = Modifier.Companion
                            .size(48.dp)
                            .then(
                                if (isCircularIcon) Modifier.Companion.clip(CircleShape)
                                else Modifier.Companion.clip(
                                    androidx.compose.foundation.shape.RoundedCornerShape(
                                        0.dp
                                    )
                                )
                            )
                    ) {
                        Image(
                            painter = it,
                            contentDescription = null,
                            modifier = Modifier.Companion.fillMaxSize(),
                            contentScale = ContentScale.Companion.Crop,
                            alpha = 1.0f
                        )
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Companion.Light),
                    modifier = Modifier.Companion.weight(1f)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = detailedInfo,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Companion.Normal),
                    modifier = Modifier.Companion.padding(
                        start = 64.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                )
            }
        }
    }

    @Composable
    fun projectInfoCard(
        modifier: Modifier = Modifier.Companion,
        titleText: String,
        descriptionText: String,
        additionalInfoText: String,
        onClick: () -> Unit,
        iconPainter: Painter? = null
    ) {
        ElevatedCard(
            modifier = modifier
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.outline,
                    androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Companion.Start,
                verticalArrangement = Arrangement.Top
            ) {
                if (iconPainter != null) {
                    Image(
                        painter = iconPainter,
                        contentDescription = "App Icon",
                        modifier = Modifier.Companion.size(128.dp)
                    )
                    Spacer(modifier = Modifier.Companion.height(8.dp))
                }

                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Companion.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.Companion.height(4.dp))

                Text(
                    text = descriptionText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Companion.Normal),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.Companion.height(8.dp))

                Text(
                    text = additionalInfoText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Companion.Light),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    @Composable
    fun UserAgreementDialog(
        modifier: Modifier,
        title: String,
        content: String,
        confirmButtonText: String,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            },
            text = {
                LazyColumn {
                    item {
                        Text(text = content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(confirmButtonText, style = MaterialTheme.typography.labelLarge)
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            modifier = modifier
        )
    }
}