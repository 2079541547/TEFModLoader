package silkways.terraria.efmodloader.ui.gametool

import android.content.Context
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.LanguageHelper.getLanguageAsNumber


object Language {

    @JvmStatic
    fun getClose(context: Context): String{
        return when(JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.languageKey)){
            0 -> {
                when(getLanguageAsNumber(context)){
                    1 -> "关闭"
                    2 -> "关闭"
                    3 -> "关闭"
                    4 -> "Close"
                    else -> "关闭"
                }
            }

            1 -> "关闭"
            2 -> "关闭"
            3 -> "关闭"
            4 -> "Close"
            else -> "关闭"
        }
    }

    @JvmStatic
    fun getStatus(context: Context): String {
        return when (JsonConfigModifier.readJsonValue(
            context,
            Settings.jsonPath,
            Settings.languageKey
        )) {
            0 -> {
                when (getLanguageAsNumber(context)) {
                    1 -> "运行状态"
                    2 -> "关闭"
                    3 -> "关闭"
                    4 -> "Close"
                    else -> "关闭"
                }
            }

            1 -> "运行状态"
            2 -> "关闭"
            3 -> "关闭"
            4 -> "Close"
            else -> "关闭"
        }
    }

    @JvmStatic
    fun getOnlineVideo(context: Context): String {
        return when (JsonConfigModifier.readJsonValue(
            context,
            Settings.jsonPath,
            Settings.languageKey
        )) {
            0 -> {
                when (getLanguageAsNumber(context)) {
                    1 -> "在线视频"
                    2 -> "关闭"
                    3 -> "关闭"
                    4 -> "Close"
                    else -> "关闭"
                }
            }

            1 -> "在线视频"
            2 -> "关闭"
            3 -> "关闭"
            4 -> "Close"
            else -> "关闭"
        }
    }

    @JvmStatic
    fun getLog(context: Context): String {
        return when (JsonConfigModifier.readJsonValue(
            context,
            Settings.jsonPath,
            Settings.languageKey
        )) {
            0 -> {
                when (getLanguageAsNumber(context)) {
                    1 -> "运行日志"
                    2 -> "关闭"
                    3 -> "关闭"
                    4 -> "Close"
                    else -> "关闭"
                }
            }

            1 -> "运行日志"
            2 -> "关闭"
            3 -> "关闭"
            4 -> "Close"
            else -> "关闭"
        }
    }


}