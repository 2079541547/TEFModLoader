package silkways.terraria.efmodloader

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MainApplication: Application() {

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

        //50

        /*
        //DynamicColors.applyToActivitiesIfAvailable(this)
        File("${this.getExternalFilesDir(null)}/TEFModLoader/").mkdirs()

        //加载文件系统
        System.loadLibrary("EFMod")

        //设置语言&主题
        AppCompatDelegate.setDefaultNightMode(SPUtils.readInt(Settings.themeKey, -1))

        LanguageUtils(
            this,
            LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), this),
            ""
        ).loadJsonFromAsset()

        val isDarkThemeEnabled = ApplicationSettings.isDarkThemeEnabled(this)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )


        //LanguageHelper.setAppLanguage(this, LanguageHelper.getAppLanguage(SPUtils.readInt(Settings.languageKey, 0), this))
         */
    }

}