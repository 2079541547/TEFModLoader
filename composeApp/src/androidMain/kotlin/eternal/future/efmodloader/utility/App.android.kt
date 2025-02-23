package eternal.future.efmodloader.utility

import android.os.Build
import eternal.future.efmodloader.MainActivity
import eternal.future.efmodloader.MainApplication

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