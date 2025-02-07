package silkways.terraria.efmodloader

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

@SuppressLint("StaticFieldLeak")
private val context: Context = MainApplication.getContext()
private const val CONFIG_FILE = "TEFModLoaderConfig"
private fun getSP(): SharedPreferences {
    return context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
}


actual object configuration {

    actual fun getString(key: String, default: String): String {
        return getSP().getString(key, default).toString()
    }

    actual fun setString(key: String, value: String) {
        getSP().edit { putString(key, value) }
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean {
        return getSP().getBoolean(key, default)
    }

    actual fun setBoolean(key: String, value: Boolean) {
        getSP().edit { putBoolean(key, value) }
    }

    actual fun getInt(key: String, default: Int): Int {
        return getSP().getInt(key, default)
    }

    actual fun setInt(key: String, value: Int) {
        getSP().edit { putInt(key, value) }
    }

}