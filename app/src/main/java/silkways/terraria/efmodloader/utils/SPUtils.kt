package silkways.terraria.efmodloader.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import silkways.terraria.efmodloader.MainApplication
import silkways.terraria.efmodloader.logic.EFLog

/**
 * SPUtils 是一个用于管理 SharedPreferences 的工具类。
 * 它提供了读取和写入布尔值、整数值和字符串值的方法。
 */
class SPUtils {

    companion object {

        // 日志标签，用于标识日志来源
        private const val TAG = "SPUtils"

        // 获取应用程序上下文
        @SuppressLint("StaticFieldLeak")
        private val context: Context = MainApplication.getContext()

        // SharedPreferences 文件名
        private const val CONFIG_FILE = "TEFModLoaderConfig"

        /**
         * 获取 SharedPreferences 实例。
         *
         * @return SharedPreferences 实例
         */
        private fun getSP(): SharedPreferences {
            return context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
        }

        /**
         * 读取布尔值。
         *
         * @param key 键
         * @param default 默认值
         * @return 读取的布尔值，如果读取失败则返回默认值
         */
        fun readBoolean(key: String, default: Boolean): Boolean {
            return try {
                val value = getSP().getBoolean(key, default)
                EFLog.d("读取布尔值: $key = $value")
                value
            } catch (e: Exception) {
                EFLog.e("读取布尔值失败: $key, ${e.message}")
                default
            }
        }

        /**
         * 写入布尔值。
         *
         * @param key 键
         * @param value 值
         */
        fun putBoolean(key: String, value: Boolean) {
            try {
                getSP().edit().putBoolean(key, value).apply()
                EFLog.d("写入布尔值: $key = $value")
            } catch (e: Exception) {
                EFLog.e("写入布尔值失败: $key, ${e.message}")
            }
        }

        /**
         * 读取整数值。
         *
         * @param key 键
         * @param default 默认值
         * @return 读取的整数值，如果读取失败则返回默认值
         */
        fun readInt(key: String, default: Int): Int {
            return try {
                val value = getSP().getInt(key, default)
                EFLog.d("读取整数值: $key = $value")
                value
            } catch (e: Exception) {
                EFLog.e("读取整数值失败: $key, ${e.message}")
                default
            }
        }

        /**
         * 写入整数值。
         *
         * @param key 键
         * @param value 值
         */
        fun putInt(key: String, value: Int) {
            try {
                getSP().edit().putInt(key, value).apply()
                EFLog.d("写入整数值: $key = $value")
            } catch (e: Exception) {
                EFLog.e("写入整数值失败: $key, ${e.message}")
            }
        }

        /**
         * 读取字符串值。
         *
         * @param key 键
         * @param default 默认值
         * @return 读取的字符串值，如果读取失败则返回默认值
         */
        fun readString(key: String, default: String?): String? {
            return try {
                val value = getSP().getString(key, default)
                EFLog.d("读取字符串值: $key = $value")
                value
            } catch (e: Exception) {
                EFLog.e("读取字符串值失败: $key, ${e.message}")
                default
            }
        }

        /**
         * 写入字符串值。
         *
         * @param key 键
         * @param value 值
         */
        fun putString(key: String, value: String) {
            try {
                getSP().edit().putString(key, value).apply()
                EFLog.d("写入字符串值: $key = $value")
            } catch (e: Exception) {
                EFLog.e("写入字符串值失败: $key, ${e.message}")
            }
        }
    }
}