package silkways.terraria.efmodloader.utility

import androidx.compose.ui.graphics.ImageBitmap

expect object Image {
    fun convertToComposeImage(bitmap: Any): ImageBitmap?
}