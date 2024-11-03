package eternal.future.efmodloader.load;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

public class Loader {

    private static final String TAG = "EFModLoader";
    private static final Map<String, String> archToLib = new HashMap<String, String>(4);

    private static String PackName = "包名";
    private static String LoaderPath;
    private static String EFModX_Path;

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void load() {

        log("EFModLoader启动中...");

        try {
            archToLib.put("arm", "armeabi-v7a");
            archToLib.put("arm64", "arm64-v8a");
            //archToLib.put("x86", "x86");
            //archToLib.put("x86_64", "x86_64");

            ClassLoader cl = Objects.requireNonNull(Loader.class.getClassLoader());
            Class<?> VMRuntime = Class.forName("dalvik.system.VMRuntime");
            Method getRuntime = VMRuntime.getDeclaredMethod("getRuntime");
            getRuntime.setAccessible(true);
            Method vmInstructionSet = VMRuntime.getDeclaredMethod("vmInstructionSet");
            vmInstructionSet.setAccessible(true);
            String arch = (String) vmInstructionSet.invoke(getRuntime.invoke(null));
            String libName = archToLib.get(arch);

            Log.i(TAG, "来自嵌入式的Bootstrap加载器");

            try {
                log("尝试加载assets中的data库");
                loadSoFromInputStream(cl.getResourceAsStream("assets/EFModLoader/" + libName + "/libdata.so"), "data");
            } catch (Exception e) {
                log("尝试加载data库");
                System.loadLibrary("data");
            }

            log(agreement());

            PackName = getPackName();
            LoaderPath = "data/data/" + PackName + "/cache/EFModLoader/loader";
            EFModX_Path = "data/data/" + PackName + "/cache/EFModX";

            if (new File(LoaderPath).exists()) {
                log("加载内核: " + LoaderPath);
                System.load(LoaderPath);
            } else {
                try {
                    log("尝试加载assets中的内核");
                    loadSoFromInputStream(cl.getResourceAsStream("assets/EFModLoader/" + libName + "/libloader.so"), "data");
                } catch (Exception e) {
                    log("加载assets中的内核失败: ", e);
                    log("尝试加载默认内核...");
                    System.loadLibrary("loader");
                }
            }

            log("加载独立Mod: " + EFModX_Path);
            loadEFModX(EFModX_Path);
        } catch (Exception e) {
            log("加载EFModLoader失败", e);
        }

        log("完成加载EFModLoader");
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static void loadEFModX(String path) {
        log("开始加载EFModX: " + path);

        File directory = new File(path);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        log("加载文件: " + file.getAbsolutePath());
                        System.load(file.getAbsolutePath());
                    } catch (UnsatisfiedLinkError e) {
                        log("无法加载文件: " + file.getAbsolutePath() + ", 错误: " + e.getMessage(), e);
                    } catch (SecurityException e) {
                        log("没有权限加载文件: " + file.getAbsolutePath() + ", 错误: " + e.getMessage(), e);
                    }
                }
            } else {
                log("没有找到任何文件在路径: " + path);
            }
        } else {
            log("指定的路径不是一个目录: " + path);
        }

        log("完成加载EFModX");
    }

    public static native String getPackName();
    public static native String agreement();

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void loadSoFromInputStream(InputStream inputStream, String soFileName) throws IOException {
        // 创建临时文件
        File tempSoFile = File.createTempFile(soFileName, ".so");
        tempSoFile.deleteOnExit(); // 确保临时文件在程序退出时删除

        // 将输入流写入临时文件
        try (FileOutputStream fos = new FileOutputStream(tempSoFile)) {
            byte[] buffer = new byte[8096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        } finally {
            inputStream.close(); // 关闭输入流
        }

        // 加载 .so 文件
        System.load(tempSoFile.getAbsolutePath());
    }


    private static void log(String message) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // 获取调用者的堆栈信息
        StackTraceElement caller = stackTraceElements[3];
        String className = caller.getClassName();
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        Log.d(TAG, String.format("%s.%s(%d): %s", className, methodName, lineNumber, message));
    }

    private static void log(String message, Throwable e) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // 获取调用者的堆栈信息
        StackTraceElement caller = stackTraceElements[3];
        String className = caller.getClassName();
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        Log.e(TAG, String.format("%s.%s(%d): %s", className, methodName, lineNumber, message), e);
    }
}