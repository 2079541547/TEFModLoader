package silkways.terraria.efmodloader.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import silkways.terraria.efmodloader.logic.ApplicationSettings.isDarkThemeEnabled
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.terminal.TerminalManager
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import silkways.terraria.efmodloader.utils.SPUtils

class TerminalActivity : EFActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)

        setContent {
            TEFModLoaderComposeTheme(darkTheme = isDarkThemeEnabled(this)) {
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

    init {
        // Add welcome message
        historyOutput.add(AnnotatedString("Welcome to TEFML Terminal\nType 'help' for a list of commands"))
    }

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
                        historyOutput.add(AnnotatedString("$command >> "))
                        historyOutput.add(AnnotatedString(result.output))
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

    private fun scrollToBottom() {
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
            TopAppBar(
                title = {
                    Text(
                        text = silkways.terraria.efmodloader.ui.utils.LanguageUtils(
                            MainApplication.getContext(),
                            LanguageHelper.getLanguage(
                                SPUtils.readInt(Settings.languageKey, 0),
                                MainApplication.getContext()
                            ),
                            "terminal"
                        ).getString("title"), color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(padding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Black)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        state = listState
                    ) {
                        items(viewModel.historyOutput.size) { index ->
                            Text(
                                text = viewModel.historyOutput[index],
                                fontSize = 14.sp,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = viewModel.inputCommand,
                        onValueChange = { newCommand ->
                            viewModel.inputCommand = newCommand
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Black)
                            .padding(
                                start = 8.dp,
                                end = 8.dp,
                                top = 4.dp,
                                bottom = 4.dp
                            ),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            viewModel.executeCommand(
                                viewModel.inputCommand
                            )
                        }),
                        decorationBox = { innerTextField ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TEFML@${Build.DEVICE}>> ",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    )
}
