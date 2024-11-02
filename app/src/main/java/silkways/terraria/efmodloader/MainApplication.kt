package silkways.terraria.efmodloader

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import silkways.terraria.efmodloader.data.GameSettings
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.ApplicationSettings
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.utils.FileUtils
import silkways.terraria.efmodloader.utils.SPUtils
import java.io.File

class MainApplication : Application() {


    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: Context

        fun getContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        DynamicColors.applyToActivitiesIfAvailable(this)
        File("${this.getExternalFilesDir(null)}/TEFModLoader/").mkdirs()

        //创建配置
        JsonConfigModifier.createJsonConfig(this, Settings.jsonPath, Settings.Data)
        JsonConfigModifier.createJsonConfig(this, GameSettings.jsonPath, GameSettings.Data)

        //检查并修补配置
        JsonConfigModifier.updateJsonKeys(this, Settings.jsonPath, Settings.Data)
        JsonConfigModifier.updateJsonKeys(this, GameSettings.jsonPath, GameSettings.Data)

        //加载文件系统
        System.loadLibrary("EFFileSystem")

        FileUtils.checkAndWriteFile()

        //设置语言&主题
        AppCompatDelegate.setDefaultNightMode(SPUtils.readInt(Settings.themeKey, -1))
        LanguageHelper.setAppLanguage(this, SPUtils.readString(Settings.languageKey, "zh")!!)

    }
}