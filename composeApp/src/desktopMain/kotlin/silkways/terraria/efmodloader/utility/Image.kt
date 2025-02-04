package silkways.terraria.efmodloader.utility

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skia.Bitmap

actual object Image {
    actual fun convertToComposeImage(bitmap: Any): ImageBitmap? {
        return when (bitmap) {
            is Bitmap -> bitmap.asComposeImageBitmap()
            else -> null
        }
    }
}