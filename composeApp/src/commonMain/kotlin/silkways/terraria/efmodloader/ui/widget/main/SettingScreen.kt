package silkways.terraria.efmodloader.ui.widget.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

object SettingScreen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Selector(
        title: String,
        defaultSelectorId: Int,
        selectorMap: Map<Int, String>,
        modifier: Modifier,
        onClick: (Int) -> Unit = {}
    ) {
        var selectedLanguageId by remember { mutableStateOf(defaultSelectorId) }
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.padding(10.dp)
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectorMap[selectedLanguageId] ?: "",
                onValueChange = {},
                label = { Text(title) },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                modifier = modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                selectorMap.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(text = name) },
                        onClick = {
                            onClick(id)
                            selectedLanguageId = id
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun selectorWithIcon(
        title: String,
        defaultSelectorId: Int = 0,
        selector: Map<Int, Pair<String, ImageVector>>,
        modifier: Modifier,
        onClick: (Int) -> Unit = {}
    ) {
        val validDefaultThemeId = if (selector.containsKey(defaultSelectorId)) defaultSelectorId else selector.keys.firstOrNull() ?: 0
        var selectedThemeId by remember { mutableStateOf(validDefaultThemeId) }

        var expanded by remember { mutableStateOf(false) }
        val icon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.padding(10.dp)
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selector[selectedThemeId]?.first ?: "",
                onValueChange = {},
                label = { Text(title) },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = selector[selectedThemeId]?.second ?: Icons.Default.MoreVert,
                            contentDescription = "Theme Icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = icon,
                            contentDescription = "Expand",
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    }
                },
                modifier = modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                selector.forEach { (id, pair) ->
                    DropdownMenuItem(
                        onClick = {
                            selectedThemeId = id
                            onClick(id)
                            expanded = false
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = pair.second, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = pair.first)
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun PathInputWithFilePicker(
        title: String,
        path: String,
        onPathChange: (String) -> Unit,
        onFolderSelect: () -> Unit,
        modifier: Modifier
    ) {
        OutlinedTextField(
            value = path,
            onValueChange = onPathChange,
            label = { Text(title) },
            trailingIcon = {
                IconButton(onClick = onFolderSelect) {
                    Icon(Icons.Default.Folder, contentDescription = "Select Folder")
                }
            },
            modifier = modifier
        )
    }

    @Composable
    fun SettingsSwitchItem(
        iconOn: ImageVector? = null,
        iconOff: ImageVector? = null,
        title: String,
        contentDescription: String = "",
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                val currentIcon = when {
                    checked && iconOn != null -> iconOn
                    !checked && iconOff != null -> iconOff
                    iconOn != null -> iconOn
                    else -> iconOff
                }

                currentIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    if (contentDescription.isNotEmpty()) {
                        Text(
                            text = contentDescription,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }

    @Composable
    fun ModernCheckBox(
        modifier: Modifier = Modifier,
        icon: ImageVector? = null,
        title: String,
        contentDescription: String,
        isChecked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    if (contentDescription.isNotEmpty()) {
                        Text(
                            text = contentDescription,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    uncheckedColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}