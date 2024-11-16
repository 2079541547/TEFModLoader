package silkways.terraria.efmodloader.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.rememberNavHostEngine
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.logic.terminal.CommandParser
import silkways.terraria.efmodloader.ui.screen.NavGraphs
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import silkways.terraria.efmodloader.ui.utils.LocalSnackbarHost

class TerminalActivity : EFActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)

        setContent {
            TEFModLoaderComposeTheme{
                val navController = rememberNavController()
                val snackBarHostState = remember { SnackbarHostState() }
                val navHostEngine = rememberNavHostEngine(
                    navHostContentAlignment = Alignment.TopCenter,
                    rootDefaultAnimations = RootNavGraphDefaultAnimations(
                        enterTransition = { fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) },
                        exitTransition = { fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) }
                    ),
                    defaultAnimationsForNestedNavGraph = mapOf(
                        NavGraphs.root to NestedNavGraphDefaultAnimations(
                            enterTransition = { fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) },
                            exitTransition = { fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) }
                        )
                    )
                )

                Scaffold(
                    snackbarHost = { SnackbarHost(snackBarHostState) }
                ) { paddingValues ->
                    CompositionLocalProvider(LocalSnackbarHost provides snackBarHostState) {
                        DestinationsNavHost(
                            modifier = Modifier.padding(paddingValues),
                            navGraph = NavGraphs.root,
                            navController = navController,
                            engine = navHostEngine
                        )
                        TerminalComposable()
                    }
                }
            }
        }
    }
}

class TerminalViewModel : ViewModel() {
    var inputCommand = ""
    var historyOutput = StringBuilder()

    fun executeCommand(command: String) {
        if (command == "clear") {
            historyOutput = StringBuilder()
        } else {
            viewModelScope.launch {
                val context = CommandParser(MainApplication.getContext()) // 假设CommandParser构造函数接受Context参数
                val executionResult = context.parseAndExecute(command)
                historyOutput.append("\n").append(executionResult)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalComposable() {
    val viewModel: TerminalViewModel = viewModel()

    val commands = listOf("help", "exit", "clear") // 手动定义命令列表
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            TopAppBar(
                title = { Text(text = "Terminal") }
            )
            TextField(
                value = viewModel.inputCommand,
                onValueChange = { viewModel.inputCommand = it },
                label = { Text(text = "Enter command") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                keyboardActions = KeyboardActions(onDone = { viewModel.executeCommand(viewModel.inputCommand) })
            )
            Button(
                onClick = { viewModel.executeCommand(viewModel.inputCommand) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Text(text = "Run Command")
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                val historyLines = viewModel.historyOutput.toString().split("\n")
                items(historyLines.size) { index ->
                    Text(text = historyLines[index], fontSize = 16.sp)
                }
            }
        }
    }
}

// 读取assets中的JSON文件
fun readJsonFromAssets(context: Context, fileName: String): JSONObject? {
    try {
        val assetManager = context.assets
        val inputStream = assetManager.open(fileName)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return JSONObject(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

// 读取assets中的JSON数组文件
fun readJsonArrayFromAssets(context: Context, fileName: String): JSONArray? {
    try {
        val assetManager = context.assets
        val inputStream = assetManager.open(fileName)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return JSONArray(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

