package eternal.future;

import static eternal.future.State.EFMod;
import static eternal.future.State.Modx;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;

public class Loader {

    public static void initialize() {
        loadModx();
        loadEFModLoader();
    }

    private static void loadModx() {

        Log.d("Path", "EFMod_c Path: " + State.EFMod_c);
        Log.d("Path", "Modx Path: " + State.Modx.getAbsolutePath());
        Log.d("Path", "EFMod Path: " + State.EFMod.getAbsolutePath());

        loadSoFilesFromDirectory(Modx);
    }

    private static void loadEFModLoader() {
        loadSpecificSoFiles(EFMod, "loader-core");
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static void loadSoFilesFromDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    loadSoFilesFromDirectory(file);
                } else if (file.isFile()) {
                    try {
                        System.load(file.getAbsolutePath());
                        System.out.println("Load:" + file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static void loadSpecificSoFiles(File baseDirPath, String soName) {
        if (!baseDirPath.exists() || !baseDirPath.isDirectory()) {
            return;
        }

        File[] uids = baseDirPath.listFiles();
        if (uids != null) {
            for (File uid : uids) {
                if (uid.isDirectory()) {
                    File soFile = new File(uid, soName);
                    if (soFile.exists() && soFile.isFile()) {
                        try {
                            System.load(soFile.getAbsolutePath());
                            System.out.println("Load:" + soFile.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}