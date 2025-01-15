package silkways.terraria.efmodloader

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import silkways.terraria.efmodloader.debug.TerminalScreen


@Composable
@Preview
fun App() {
    MaterialTheme {
        TerminalScreen()
    }
}