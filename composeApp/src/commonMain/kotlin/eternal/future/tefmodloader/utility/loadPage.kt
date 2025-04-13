package eternal.future.tefmodloader.utility

import androidx.compose.runtime.Composable
import java.io.File

@Composable
expect fun LoadAndDisplayPage(
    filePath: File,
    className: String,
    params: Map<String, *>)

@Composable
fun loadPageFromFile(filePath: File, className: String, params: Map<String, *>) { LoadAndDisplayPage(filePath, className, params) }