package eternal.future;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import eternal.future.utility.AssetManager;
import eternal.future.utility.FileUtils;

public class TEFModLoader extends Activity {
    private static final int REQUEST_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new File(this.getFilesDir(), "TEFModLoader_jni").mkdirs(); //用于判断是否有java依赖

        try {
            String jsonString = AssetManager.readTextOfAsset(this, "config.json");
            JSONObject obj = new JSONObject(jsonString);
            State.Mode = obj.getInt("mode");
            State.gameActivity = Class.forName(obj.getString("activity"));
            State.ManagerPackName = obj.getString("manager");
        } catch (JSONException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        State.Modx = new File(getFilesDir(), "TEFModLoader/Modx");
        State.EFMod = new File(getFilesDir(), "TEFModLoader/EFMod");


        if (State.Mode == 0) {
            checkPermission();
        } else {
            State.EFMod_c = new File(Environment.getExternalStorageDirectory(), "Android/" + State.ManagerPackName + "/files/EFMod").getAbsolutePath();
            startGame();
        }
    }

    private void startGame() {

        if (State.Mode == 0) {
           if (State.Modx.exists()) {
               FileUtils.deleteDirectory(State.Modx);
           }
           if (State.EFMod.exists()) {
               FileUtils.deleteDirectory(State.EFMod);
           }

            State.EFMod.mkdirs();
            State.Modx.mkdirs();

            State.Modx_external = new File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader/Modx");
            State.EFMod_external = new File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader/EFMod");
            State.EFMod_c = new File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader/Data/EFMod").getAbsolutePath();

            if (State.EFMod_external.exists()) {
                FileUtils.moveContent(State.EFMod_external, State.EFMod);
            }

            if (State.Modx_external.exists()) {
                FileUtils.moveContent(State.Modx_external, State.Modx);
            }
        }

        Intent gameActivity = new Intent(this, State.gameActivity);
        startActivity(gameActivity);
        Loader.initialize();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                startGame();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else {
            String readPermission = "android.permission.READ_EXTERNAL_STORAGE";
            String writePermission = "android.permission.WRITE_EXTERNAL_STORAGE";

            if (checkSelfPermission(readPermission) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(writePermission) == PackageManager.PERMISSION_GRANTED) {
                startGame();
            } else {
                requestPermissions(new String[]{readPermission, writePermission}, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (!allPermissionsGranted) {
                checkPermission();
            } else {
                startGame();
            }
        }
    }
}