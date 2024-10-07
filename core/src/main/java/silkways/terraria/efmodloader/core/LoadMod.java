package silkways.terraria.efmodloader.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.bytedance.shadowhook.ShadowHook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class LoadMod {

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public void LoadMian(Context context, Boolean isC , String path){
        loadHook();
        if (isC) {
            System.load(path);
        } else {
            System.loadLibrary("TEFModLoader");
        }
    }

    public void loadHook(){
        ShadowHook.init(new ShadowHook.ConfigBuilder()
                .setMode(ShadowHook.Mode.UNIQUE)
                .build());
    }



}