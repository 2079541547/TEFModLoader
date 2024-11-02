package silkways.terraria.efmodloader.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import silkways.terraria.efmodloader.MainApplication

class SPUtils {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private val context: Context = MainApplication.getContext()

        private const val CONFIG_FILE = "TEFModLoaderConfig"

        private fun getSP(): SharedPreferences {
            return context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
        }

        fun readBoolean(key: String, default: Boolean): Boolean {
            return getSP().getBoolean(key, default)
        }

        fun putBoolean(key: String, value: Boolean) {
            getSP().edit().putBoolean(key, value).apply()
        }

        fun readInt(key: String, default: Int): Int {
            return getSP().getInt(key, default)
        }

        fun putInt(key: String, value: Int) {
            getSP().edit().putInt(key, value).apply()
        }

        fun readString(key: String, default: String?): String? {
            return getSP().getString(key, default)
        }

        fun putString(key: String, value: String) {
            getSP().edit().putString(key, value).apply()
        }
    }
}
