package silkways.terraria.toolbox;

import android.app.Application;

import com.bytedance.shadowhook.ShadowHook;

public class ToolBox extends Application {

    private static final String TAG = "AssetManagerHooker";


    static {
        System.loadLibrary("Redirect");
    }

    public static void init() {
        ShadowHook.init(new ShadowHook.ConfigBuilder()
                .setMode(ShadowHook.Mode.UNIQUE)
                .build());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();

        if(doHook()){
            System.out.print("成功");
        } else {
            System.out.print("丸辣");
        }

        doHook();


    }

    public native boolean doHook();



}
