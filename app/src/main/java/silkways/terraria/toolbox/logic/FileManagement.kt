package silkways.terraria.toolbox.logic

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File

object FileManagement {
    fun openWithOtherApp(context: Context, path: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(
            Uri.fromFile(File(path)).toString()))
        intent.setDataAndType(Uri.fromFile(File(path)), mimeType)
        context.startActivity(intent)
    }
}