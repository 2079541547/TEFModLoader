package silkways.terraria.efmodloader

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
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
        //DynamicColors.applyToActivitiesIfAvailable(this)
        File("${this.getExternalFilesDir(null)}/TEFModLoader/").mkdirs()

        //加载文件系统
        System.loadLibrary("EFFileSystem")

        //设置语言&主题
        AppCompatDelegate.setDefaultNightMode(SPUtils.readInt(Settings.themeKey, -1))

        LanguageUtils(
            this,
            LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), this),
            ""
        ).loadJsonFromAsset()
        //LanguageHelper.setAppLanguage(this, LanguageHelper.getAppLanguage(SPUtils.readInt(Settings.languageKey, 0), this))
    }

}