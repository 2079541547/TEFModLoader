package eternal.future.efmodloader.load;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import java.io.File;
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
    private static String PackName;
    private static String LoaderPath;
    private static String EFModX_Path;

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void load() {

        log("EFModLoader启动中...");

        try {
            ClassLoader cl = Objects.requireNonNull(Loader.class.getClassLoader());

            try {
                log("尝试加载assets中的data库");
                System.load(cl.getResource("assets/EFModLoader/" + Build.CPU_ABI + "/libdata.so").getPath().substring(5));
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
                    //System.loadLibrary("efmodloader");
                    System.load(cl.getResource("assets/EFModLoader/" + Build.CPU_ABI + "/libloader.so").getPath().substring(5));
                } catch (Exception e) {
                    log("加载assets中的内核失败: ", e);
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