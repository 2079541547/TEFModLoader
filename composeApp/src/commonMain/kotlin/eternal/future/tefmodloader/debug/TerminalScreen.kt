package eternal.future.tefmodloader.debug

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


@Composable
fun TerminalScreen() {
    var commands by remember { mutableStateOf(listOf<Command>()) }
    var inputText by remember { mutableStateOf("") }
    var showSearchDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            CommandsList(commands = commands, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(8.dp))
            CommandInputWithTools(
                text = inputText,
                onTextChanged = { inputText = it },
                onExecute = {
                    if (inputText.isNotBlank()) {
                        val parsedCommand = parseCommand(inputText)
                        commands += parsedCommand
                        inputText = ""
                    }
                },
                onClearHistory = { commands = listOf() },
                onShowSearchDialog = { showSearchDialog = true }
            )
        }
        if (showSearchDialog) {
            SearchBottomSheet(
                onDismissRequest = { showSearchDialog = false },
                onSelectCommand = { command ->
                    inputText = command
                    showSearchDialog = false
                }
            )
        }
    }
}

@Composable
fun CommandsList(commands: List<Command>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(commands.size) { index ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { -40 }) + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                CommandCard(command = commands[index])
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CommandCard(command: Command, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth().padding(5.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = "> ${command.command}", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = command.output, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun CommandInputWithTools(
    text: String,
    onTextChanged: (String) -> Unit,
    onExecute: () -> Unit,
    onClearHistory: () -> Unit,
    onShowSearchDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChanged,
                label = { Text("Enter command", color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onExecute() }),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onExecute) {
                Text("Execute", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClearHistory) {
                Icon(Icons.Default.Delete, contentDescription = "Clear History")
            }
            IconButton(onClick = onShowSearchDialog) {
                Icon(Icons.Default.Search, contentDescription = "Search Commands")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(onDismissRequest: () -> Unit, onSelectCommand: (String) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Commands", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            var searchQuery by remember { mutableStateOf("") }
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search command", color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            val filteredCommands = listOf("hello", "date", "clear", "exit").filter { it.contains(searchQuery, ignoreCase = true) }
            LazyColumn {
                items(filteredCommands.size) { index ->
                    Text(
                        text = filteredCommands[index],
                        modifier = Modifier.fillMaxWidth()
                            .clickable {
                                onSelectCommand(filteredCommands[index])
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        onDismissRequest()
                                    }
                                }
                            }
                            .padding(8.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}