package eternal.future.efmodloader.utility

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import android.os.Bundle
import eternal.future.efmodloader.MainActivity

actual object Net {
    @SuppressLint("UseKtx")
    actual fun openUrlInBrowser(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            ContextCompat.startActivity(MainActivity.getContext(), browserIntent, Bundle())
        } catch (e: Exception) {
            EFLog.e("无法打开链接: $url, 错误: ", e)
        }
    }
}