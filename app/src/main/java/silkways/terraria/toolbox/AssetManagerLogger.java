package silkways.terraria.toolbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AssetManagerLogger {

    private static final String TAG = "AssetManagerLogger";

    /**
     * 使用反射调用 AssetManager 的 addAssetPath 方法，并记录日志。
     *
     * @param assetManager AssetManager 实例
     * @param path         要添加的路径
     */
    public static void addAssetPathWithLogging(AssetManager assetManager, String path) {
        try {
            // 获取 addAssetPath 方法
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            // 设置方法为可访问状态
            addAssetPath.setAccessible(true);

            // 记录调用前的日志
            Log.i(TAG, "Adding asset path: " + path);

            // 调用 addAssetPath 方法
            addAssetPath.invoke(assetManager, path);

            // 记录调用后的日志
            Log.i(TAG, "Asset path added: " + path);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException e) {
            Log.e(TAG, "Failed to add asset path: " + path, e);
        }
    }
}