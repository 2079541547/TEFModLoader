package silkways.terraria.toolbox

import android.app.Application
import android.content.Context
import android.webkit.WebView
import com.fvbox.lib.FCore
import com.limpoxe.fairy.core.FairyGlobal
import com.tencent.mmkv.BuildConfig


class Application: Application() {
    override fun attachBaseContext(base: Context) {

        FairyGlobal.setLogEnable(true)
        FairyGlobal.setLocalHtmlenable(false)
        if (BuildConfig.DEBUG) {
            FairyGlobal.setInstallationWithSameVersion(true)
        }
        WebView.setDataDirectorySuffix(getProcessName().replace("[.:]".toRegex(), "_"))
        super.attachBaseContext(base)
        FCore.get().init(this)
        FCore.get().setAllowSystemInteraction(true)
        FCore.get().setAutoPreloadApplication(true)
        if(FCore.isClient()) {
            return
        }
    }

    override fun onCreate() {
        super.onCreate()
        if(FCore.isClient()) {
            return
        }
    }

}

