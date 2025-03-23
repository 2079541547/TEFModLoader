package eternal.future.tefmodloader.utility

import android.os.Build
import eternal.future.tefmodloader.MainActivity
import eternal.future.tefmodloader.MainApplication

actual object App {
    actual fun exit() {
        MainActivity.exit()
    }

    actual fun getCurrentArchitecture(): String {
        return Build.CPU_ABI
    }

    actual fun getPrivate(): String {
        return MainApplication.getContext().filesDir.toString()
    }
}