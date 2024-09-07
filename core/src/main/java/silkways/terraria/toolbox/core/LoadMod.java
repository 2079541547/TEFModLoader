package silkways.terraria.toolbox.core;

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

    public void LoadMian(Context context){
        loadHook();
        System.loadLibrary("Major");
        readFileContent(context);
    }

    public void loadHook(){
        ShadowHook.init(new ShadowHook.ConfigBuilder()
                .setMode(ShadowHook.Mode.UNIQUE)
                .build());
    }

    public static String readFileContent(Context context) {
        File file = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/ToolBoxData/ModData/mod_data.json");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e("TAG", "Error reading file: ", e);
            return "";
        }
    }

    public native void getJsonContent(String content);
}