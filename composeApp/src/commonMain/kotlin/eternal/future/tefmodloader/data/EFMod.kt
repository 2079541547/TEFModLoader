package eternal.future.tefmodloader.data

import androidx.compose.ui.graphics.ImageBitmap

data class EFMod(
    val info: Info,
    val github: Github,
    val platform: Platforms,
    val loaders: List<LoaderSupport>,
    val introduce: Introduction,
    val path: String,
    var icon: ImageBitmap? = null,
    val standards: Int,
    var isEnabled: Boolean,
    var Modx: Boolean,

    val pageClass: String?

)

data class Info(
    val name: String,
    val author: String,
    val version: String,
    val page: Boolean
)

data class Github(
    val openSource: Boolean,
    val overview: String,
    val url: String
)

data class Platforms(
    val windows: PlatformSupport,
    val android: PlatformSupport
)

data class PlatformSupport(
    val arm64: Boolean,
    val arm32: Boolean,
    val x86_64: Boolean,
    val x86: Boolean
)

data class Introduction(
    val description: String
)

data class LoaderSupport(
    val name: String,
    val supportedVersions: List<String>
)