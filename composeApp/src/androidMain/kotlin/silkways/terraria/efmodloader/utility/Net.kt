package silkways.terraria.efmodloader.utility

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import android.os.Bundle
import silkways.terraria.efmodloader.MainActivity

actual object Net {
    actual fun openUrlInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/2079541547/TEFModLoader"))
        ContextCompat.startActivity(MainActivity.getContext(), browserIntent, Bundle())
    }
}