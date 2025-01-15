package silkways.terraria.efmodloader.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun TerminalScreen() {
    MaterialTheme(
        colors = darkColors(
            primary = Color.Black,
            onPrimary = Color.Green,
            surface = Color.Black,
            onSurface = Color.White
        )
    ) {
        var inputText by remember { mutableStateOf("") }
        val outputHistory = remember { mutableStateListOf<String>() }
        val terminalParser = TerminalParser()

        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(outputHistory) { line ->
                    Text(text = line, color = Color.Green)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { newText -> inputText = newText },
                    placeholder = { Text("Enter command", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Black,
                        textColor = Color.White,
                        cursorColor = Color.Green,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (inputText.isNotBlank()) {
                        if (inputText == "clear") {
                            outputHistory.clear()
                            inputText = ""
                        } else {
                            val args = inputText.trim().split("\\s+".toRegex())
                            val result = terminalParser.parser(args.toTypedArray())
                            outputHistory.add(">> $inputText \n> $result\n")
                            inputText = ""
                        }
                    }
                }) {
                    Text("Run")
                }
            }
        }
    }
}