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
        //50
    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(MainActivity.getContext(), arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1001)
            }

            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = String.format("package:%s", applicationContext.packageName).toUri()
                MainActivity.getContext().startActivityForResult(intent, 1001)
            }
        } else {
            val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
            val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

            if (ContextCompat.checkSelfPermission(MainActivity.getContext(), readPermission) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.getContext(), writePermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.getContext(), arrayOf(readPermission, writePermission), 1001)
            }
        }
    }

}