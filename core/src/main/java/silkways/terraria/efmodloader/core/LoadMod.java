package silkways.terraria.efmodloader.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.File;

public class LoadMod {

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public void LoadMian(Context context){

        String path = context.getCacheDir().getPath() + "/EFModLoader/libLoader.so";
        if (new File(path).exists()) {
            Log.i("LoadMod", "加载自定义内核");
            System.load(path);
        } else {
            Log.i("LoadMod", "加载默认内核");
            System.loadLibrary("TEFModLoader");
        }

        try {
            LoadLoadEFModX(context.getCacheDir().getPath() + "/SpecialLoading/");
        } catch (Exception ignored) {}

    }


    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private void LoadLoadEFModX(String Path) {
        File directory = new File(Path);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles((ignored, name) -> name.endsWith(".so"));
            if (files != null) {
                for (File file : files) {
                    System.out.println("加载的独立Mod: " + file.getAbsolutePath());
                    System.load(file.getAbsolutePath());
                }
            }
        }
    }

}