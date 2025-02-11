package eternal.future.efmodloader

import androidx.compose.runtime.Composable

interface ModPage_android {
    fun initialize(api: Map<String, Any>)
    @Composable
    fun android()
}

interface ModPage_desktop {
    fun initialize(api: Map<String, Any>)
    @Composable
    fun desktop()
}
