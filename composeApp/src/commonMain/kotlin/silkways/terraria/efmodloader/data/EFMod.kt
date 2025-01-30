package silkways.terraria.efmodloader.data

import org.jetbrains.skia.Bitmap


data class EFMod(
    val info: ModInfo,
    val filePath: String,
    var icon: Bitmap?,
    var isEnabled: Boolean
) {
    data class ModInfo(
        val name: String,
        val author: String,
        val version: String,
        val introduce: String,
        val github: GithubInfo,
        val mod: ModDetails
    )

    data class GithubInfo(
        val openSource: Boolean,
        val overview: String,
        val url: String
    )

    data class ModDetails(
        val Modx: Boolean,
        val privateData: Boolean,
        val page: Boolean,
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