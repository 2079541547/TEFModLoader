package eternal.future.tefmodloader

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import eternal.future.tefmodloader.utility.EFLog

@SuppressLint("StaticFieldLeak")
private val context: Context = MainApplication.getContext()
private const val CONFIG_FILE = "TEFModLoaderConfig"
@SuppressLint("WrongConstant")
private fun getSP(): SharedPreferences {
    EFLog.d("正在获取SharedPreferences配置文件: $CONFIG_FILE")
    return context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
}

actual object configuration {

    actual fun getString(key: String, default: String): String {
        EFLog.d("正在读取字符串配置 [键: $key, 默认值: $default]")
        val result = getSP().getString(key, default).toString()
        EFLog.v("读取完成 [键: $key, 结果值: $result]")
        return result
    }

    actual fun setString(key: String, value: String) {
        EFLog.d("正在写入字符串配置 [键: $key, 值: $value]")
        getSP().edit { putString(key, value) }
        EFLog.v("字符串配置写入成功 [键: $key]")
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean {
        EFLog.d("正在读取布尔值配置 [键: $key, 默认值: $default]")
        val result = getSP().getBoolean(key, default)
        EFLog.v("读取完成 [键: $key, 结果值: $result]")
        return result
    }

    actual fun setBoolean(key: String, value: Boolean) {
        EFLog.d("正在写入布尔值配置 [键: $key, 值: $value]")
        getSP().edit { putBoolean(key, value) }
        EFLog.v("布尔值配置写入成功 [键: $key]")
    }

    actual fun getInt(key: String, default: Int): Int {
        EFLog.d("正在读取整型配置 [键: $key, 默认值: $default]")
        val result = getSP().getInt(key, default)
        EFLog.v("读取完成 [键: $key, 结果值: $result]")
        return result
    }

    actual fun setInt(key: String, value: Int) {
        EFLog.d("正在写入整型配置 [键: $key, 值: $value]")
        getSP().edit { putInt(key, value) }
        EFLog.v("整型配置写入成功 [键: $key]")
    }
}