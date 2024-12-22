/*******************************************************************************
 * 文件名称: Loader
 * 项目名称: EFModLoader
 * 创建时间: 2024/11/2 14:18
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

package eternal.future.efmodloader.load;

import static eternal.future.efmodloader.load.FileUtils.copyFilesFromTo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Loader {
    private static final String TAG = "EFModLoader";
    private static final String RUNTIME = "TEFModLoader";
    private static final Map<String, String> archToLib = new HashMap<>(4);

    static {
        archToLib.put("arm", "armeabi-v7a");
        archToLib.put("arm64", "arm64-v8a");
        archToLib.put("x86", "x86");
        archToLib.put("x86_64", "x86_64");
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void load() {
        try {
            ClassLoader cl = Loader.class.getClassLoader();
            Class<?> VMRuntime = Class.forName("dalvik.system.VMRuntime");
            Method getRuntime = VMRuntime.getDeclaredMethod("getRuntime");
            getRuntime.setAccessible(true);
            Method vmInstructionSet = VMRuntime.getDeclaredMethod("vmInstructionSet");
            vmInstructionSet.setAccessible(true);
            String arch = (String) vmInstructionSet.invoke(getRuntime.invoke(null));
            String libName = archToLib.get(arch);

            if (cl != null) {
                log("尝试加载assets中的EFandroid库");
                loadSoFromInputStream(cl.getResourceAsStream("assets/EFModLoader/" + libName + "/libEFandroid.so"));
            } else {
                log("尝试加载lib中的EFandroid库");
                System.loadLibrary("EFandroid");
            }

            log(getAgreement());

            checkExternalMode();

            File EFModX = new File(getContext().getCacheDir(), "EFMod/Modx");
            log("EFModX：" + Arrays.toString(EFModX.listFiles()));
            loadEFModX(EFModX.getAbsolutePath());
            loadModLoader();
        } catch (Exception e) {
            log("加载EFModLoader失败", e);
        }
        log("完成加载EFModLoader");
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static void loadEFModX(String path) {
        log("开始加载EFModX: " + path);
        File dir = new File(path);
        if (dir.isDirectory()) {
            loadSoFilesFromDirectory(dir);
        } else {
            log("指定的路径不是一个目录: " + path);
        }
        log("完成加载EFModX");
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static void loadSoFilesFromDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归调用，处理子目录
                    loadSoFilesFromDirectory(file);
                } else if (file.isFile() && file.getName().toLowerCase().endsWith(".so")) {
                    try {
                        log("加载文件: " + file.getAbsolutePath());
                        System.load(file.getAbsolutePath());
                    } catch (Exception e) {
                        log("加载文件出错: " + e.getMessage(), e);
                    }
                }
            }
        } else {
            log("没有找到任何文件在路径: " + dir.getAbsolutePath());
        }
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static void loadSoFromInputStream(InputStream is) {
        try {
            File tempSoFile = File.createTempFile("EFandroid", ".so");
            tempSoFile.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(tempSoFile)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            }
            System.load(tempSoFile.getAbsolutePath());
        } catch (IOException | UnsatisfiedLinkError e) {
            log("加载so文件失败: " + "EFandroid", e);
        }
    }


    private static void checkExternalMode() {
        if (hasReadWritePermission()) {
            log("已获取权限！");
            @SuppressLint("SdCardPath") File externalDir = new File(Environment.getExternalStorageDirectory(), "Documents/TEFModLoader");
            log("外部目录: " + externalDir.getAbsolutePath());

            if (externalDir.exists()) {
                log("正在进行外部模式的额外操作...");

                File efModX = new File(getContext().getCacheDir(), "EFMod/Modx");
                File efMod = new File(getContext().getCacheDir(), "EFMod/Mod");
                File loader = new File(getContext().getCacheDir(), "Loader/");
                File modPrivate = new File(getContext().getExternalFilesDir(null), "EFMod-Private/");

                File efModXExterior = new File(externalDir, "EFMod/Modx");
                File efModExterior = new File(externalDir, "EFMod/Mod");
                File loaderExterior = new File(externalDir, "Loader/");
                File modPrivateExterior = new File(externalDir, "Private/");

                log("EFModX 目标: " + efModX.getAbsolutePath());
                log("EFMod 目标: " + efMod.getAbsolutePath());
                log("Loader 目标: " + loader.getAbsolutePath());
                log("ModPrivate 目标: " + modPrivate.getAbsolutePath());

                log("EFModX 外部: " + efModXExterior.getAbsolutePath());
                log("EFMod 外部: " + efModExterior.getAbsolutePath());
                log("Loader 外部: " + loaderExterior.getAbsolutePath());
                log("ModPrivate 外部: " + modPrivateExterior.getAbsolutePath());

                if (efMod.exists()) {
                    log("删除 EFMod: " + efMod.getAbsolutePath());
                    efMod.delete();
                }
                if (efModX.exists()) {
                    log("删除 EFModX: " + efModX.getAbsolutePath());
                    efModX.delete();
                }
                if (loader.exists()) {
                    log("删除 Loader: " + loader.getAbsolutePath());
                    loader.delete();
                }

                if (loaderExterior.exists()) {
                    log("复制 Loader: " + loaderExterior.getAbsolutePath() + " 到 " + loader.getAbsolutePath());
                    copyFilesFromTo(loaderExterior, loader);
                } else {
                    log("Loader 外部文件不存在: " + loaderExterior.getAbsolutePath());
                }

                if (efModXExterior.exists()) {
                    log("复制 EFModX: " + efModXExterior.getAbsolutePath() + " 到 " + efModX.getAbsolutePath());
                    copyFilesFromTo(efModXExterior, efModX);
                } else {
                    log("EFModX 外部文件不存在: " + efModXExterior.getAbsolutePath());
                }

                if (efModExterior.exists()) {
                    log("复制 EFMod: " + efModExterior.getAbsolutePath() + " 到 " + efMod.getAbsolutePath());
                    copyFilesFromTo(efModExterior, efMod);
                } else {
                    log("EFMod 外部文件不存在: " + efModExterior.getAbsolutePath());
                }

                if (modPrivateExterior.exists()) {
                    log("复制 ModPrivate: " + modPrivateExterior.getAbsolutePath() + " 到 " + modPrivate.getAbsolutePath());
                    copyFilesFromTo(modPrivateExterior, modPrivate);
                    copyFilesFromTo(modPrivate, new File(externalDir, "export/private"));
                } else {
                    log("ModPrivate 外部文件不存在: " + modPrivateExterior.getAbsolutePath());
                }
            } else {
                log("外部目录不存在: " + externalDir.getAbsolutePath());
            }
        } else {
            log("未获取权限！");
        }
    }

    private static boolean hasReadWritePermission() {
        Application context = getContext();
        return context.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static void loadModLoader() {
        try {
            File loader = new File(getContext().getCacheDir(), "Loader/libLoader.so");
            if (loader.exists()) {
                log("正在尝试加载EFModLoader内核...");
                System.load(loader.getAbsolutePath());
            } else {
                log("无EFModLoader内核可加载，跳过操作...");
            }
        } catch (Exception e) {
            log("加载EFModLoader内核时出现错误：", e);
        }
    }

    private static void log(String msg, Throwable t) {
        Log.e(TAG, msg, t);
    }

    private static void log(String msg) {
        Log.i(TAG, msg);
    }

    public static native Application getContext();
    public static native String getAgreement();
}