package eternal.future.tefmodloader.data

import androidx.compose.ui.graphics.ImageBitmap


data class EFModLoader(
    val info: Info,
    val github: Github,
    val platforms: Platforms,
    val loader: Loader,
    val supportModes: SupportModes,
    val compatibility: Compatibility,
    val introduces: Introduction,
    val icon: ImageBitmap? = null,
    val path: String,
    var isEnabled: Boolean
)

data class Loader(
    val libName: String
)

data class Compatibility(
    val supportedVersions: List<String>,
    val minimumStandards: Int,
    val highestStandards: Int
)


data class SupportModes(
    val android: SupportModeAndroid,
    val windows: SupportModeWindows
)

data class SupportModeAndroid(
    val inline: Boolean,
    val external: Boolean,
    val root: Boolean,
    val share: Boolean
)

data class SupportModeWindows(
    val hijack: Boolean
)