package silkways.terraria.efmodloader.data

import org.jetbrains.skia.Bitmap


data class EFModLoader(
    val info: LoaderInfo,
    val filePath: String,
    val isSelected: Boolean,
    var icon: Bitmap?
) {
    data class LoaderInfo(
        val name: String,
        val author: String,
        val version: String,
        val introduce: String,
        val github: GithubInfo,
        val loader: LoaderDetails
    )

    data class GithubInfo(
        val overview: String,
        val url: String
    )

    data class LoaderDetails(
        val platform: PlatformSupport
    )

    data class PlatformSupport(
        val Windows: PlatformArchitectures,
        val Android: PlatformArchitectures
    )

    data class PlatformArchitectures(
        val arm64: Boolean,
        val arm32: Boolean,
        val x86_64: Boolean,
        val x86_32: Boolean
    )
}
