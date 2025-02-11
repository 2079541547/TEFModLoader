package eternal.future.efmodloader

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import eternal.future.efmodloader.debug.TerminalScreen


@Composable
@Preview
fun App() {
    MaterialTheme {
        TerminalScreen()
    }
}