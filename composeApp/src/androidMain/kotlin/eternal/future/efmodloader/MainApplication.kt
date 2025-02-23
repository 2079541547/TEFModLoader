package eternal.future.efmodloader

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import eternal.future.efmodloader.utility.App
import eternal.future.efmodloader.utility.Zip
import java.io.File

class MainApplication: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: Context

        fun getContext(): Context {
            return instance.applicationContext
        }
    }


    @SuppressLint("UnsafeDynamicallyLoadedCode")
    override fun onCreate() {
        super.onCreate()
        instance = this

        File(App.getPrivate(), "SilkCasket").let {
            if (!it.exists()) {
                val zipPath = Zip.copyZipFromResources("SilkCasket.zip", "${it.parent}")
                Zip.unzipSpecificFilesIgnorePath(zipPath, it.path,  "android/${App.getCurrentArchitecture()}/libsilkcasket.so")
                File(zipPath).delete()
            }
            System.load(it.path)
            print("已加载")
        }

        State.EFModPath = File(
            this.getExternalFilesDir(null),
            "EFMod"
        ).path

        State.EFModLoaderPath = File(
            this.getExternalFilesDir(null),
            "EFModLoader"
        ).path
    }
}