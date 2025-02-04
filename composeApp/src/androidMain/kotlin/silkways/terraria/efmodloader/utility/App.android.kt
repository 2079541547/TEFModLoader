package silkways.terraria.efmodloader.utility

import android.os.Build
import silkways.terraria.efmodloader.MainActivity
import silkways.terraria.efmodloader.MainApplication

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