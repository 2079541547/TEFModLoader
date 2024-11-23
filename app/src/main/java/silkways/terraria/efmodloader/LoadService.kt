package silkways.terraria.efmodloader

import android.app.Service
import android.content.Intent
import android.os.IBinder
import eternal.future.efmodloader.load.Loader

class LoadService: Service() {

    override fun onCreate() {
        super.onCreate()
        Loader.load()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}