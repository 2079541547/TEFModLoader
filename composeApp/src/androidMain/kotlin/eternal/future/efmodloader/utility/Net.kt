package eternal.future.efmodloader.utility

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import android.os.Bundle
import eternal.future.efmodloader.MainActivity

actual object Net {
    actual fun openUrlInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/2079541547/TEFModLoader"))
        ContextCompat.startActivity(MainActivity.getContext(), browserIntent, Bundle())
    }
}