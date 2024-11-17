package silkways.terraria.efmodloader.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.terminal.LanguageUtils
import silkways.terraria.efmodloader.logic.terminal.TerminalManager
import silkways.terraria.efmodloader.ui.screen.CustomTopBar
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import silkways.terraria.efmodloader.utils.SPUtils

class TerminalActivity : EFActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)

        LanguageUtils(
            MainApplication.getContext(),
            LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
            ""
        ).loadJsonFromAsset()

        setContent {
            TEFModLoaderComposeTheme {
                TerminalComposable()
            }
        }
    }
}


@SuppressLint("MutableCollectionMutableState", "AutoboxingStateCreation")
class TerminalViewModel : ViewModel() {
    var inputCommand by mutableStateOf("")
    var historyOutput by mutableStateOf(mutableListOf<AnnotatedString>())
    var scrollState by mutableStateOf(0)
    private var scrollJob: Job? = null
    private val terminalManager = TerminalManager(context = MainApplication.getContext())

    fun executeCommand(command: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (command.lowercase()) {
                "clear" -> {
                    withContext(Dispatchers.Main) {
                        clearHistory()
                        inputCommand = ""
                    }
                }
                else -> {
                    val result = terminalManager.execute(command)
                    withContext(Dispatchers.Main) {
                        historyOutput.add(result.output)
                        inputCommand = ""
                        scrollToBottom()
                    }
                }
            }
        }
    }

    fun clearHistory() {
        historyOutput.clear()
        scrollToBottom()
    }

    fun scrollToBottom() {
        scrollJob?.cancel()
        scrollJob = viewModelScope.launch {
            delay(100) // 等待UI更新
            if (historyOutput.isNotEmpty()) {
                scrollState = historyOutput.size - 1
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalComposable() {
    val viewModel: TerminalViewModel = viewModel()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { viewModel.scrollState }
            .collect { scrollPosition ->
                if (scrollPosition >= 0) {
                    listState.animateScrollToItem(scrollPosition)
                }
            }
    }

    Scaffold(
        topBar = {
            CustomTopBar(silkways.terraria.efmodloader.ui.utils.LanguageUtils(
                MainApplication.getContext(),
                LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), MainApplication.getContext()),
                "terminal"
            ).getString("title"))
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {

                    // Device Info Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            .padding(16.dp)
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "TEFML@${Build.DEVICE}&${Build.CPU_ABI}", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Output History Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            .padding(16.dp)
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            state = listState
                        ) {
                            items(viewModel.historyOutput.size) { index ->
                                Text(
                                    text = viewModel.historyOutput[index],
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Command Buttons Section
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            .padding(16.dp)
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp)),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val commands = listOf("ls", "cd", "pwd", "whoami", "clear", "exit", "help-", "help-c")

                        items(commands.size) { index ->
                            CommandItem(cmd = commands[index], viewModel = viewModel)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            .padding(16.dp)
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = viewModel.inputCommand,
                            onValueChange = { newCommand ->
                                viewModel.inputCommand = newCommand
                            },
                            modifier = Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                .padding(12.dp),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontFamily = FontFamily.Monospace),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                            keyboardActions = KeyboardActions(onDone = { viewModel.executeCommand(viewModel.inputCommand) }),
                            decorationBox = { innerTextField ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "TEFML@${Build.DEVICE}>>", color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}




@Composable
fun CommandItem(cmd: String, viewModel: TerminalViewModel) {
    Surface(
        modifier = Modifier
            .clickable {
                viewModel.inputCommand = cmd
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cmd,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
