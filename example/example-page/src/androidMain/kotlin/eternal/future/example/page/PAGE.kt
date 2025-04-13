package eternal.future.example.page

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class PAGE {
    var params: Map<String, *> = emptyMap<String, Any>()
    val Content: (@Composable () -> Unit) = {
        Text("Hello, World!")
        Text("private-path:${params["private-path"]}")
        Text("data-path:${params["data-path"]}")
    }
}