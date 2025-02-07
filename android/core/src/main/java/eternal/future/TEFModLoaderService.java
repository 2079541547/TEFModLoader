package eternal.future;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TEFModLoaderService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        Loader.initialize();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
