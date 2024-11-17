package eternal.future.efmodloader.load;

import android.annotation.SuppressLint;


/*******************************************************************************
 * 文件名称: Log
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/9 上午4:13
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
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/


/**
 * 日志工具类，用于在Android应用中打印带有时间戳的日志。
 * 该类提供了四个静态方法，分别对应不同的日志级别：INFO、DEBUG、ERROR和WARNING。
 * 每个方法都会自动格式化日志消息，添加当前的时间戳。
 */
public class Log {

    /**
     * 打印INFO级别的日志。
     *
     * @param TAG      日志标签，用于标识日志来源。
     * @param message  要打印的日志消息。
     */
    public static void i(String TAG, String message) {
        if (message != null) {
            android.util.Log.i(TAG, formatLog(message));
        }
    }

    /**
     * 打印DEBUG级别的日志。
     *
     * @param TAG      日志标签，用于标识日志来源。
     * @param message  要打印的日志消息。
     */
    public static void d(String TAG, String message) {
        if (message != null) {
            android.util.Log.d(TAG, formatLog(message));
        }
    }

    /**
     * 打印ERROR级别的日志。
     *
     * @param TAG      日志标签，用于标识日志来源。
     * @param message  要打印的日志消息。
     */
    public static void e(String TAG, String message) {
        if (message != null) {
            android.util.Log.e(TAG, formatLog(message));
        }
    }

    /**
     * 打印WARNING级别的日志。
     *
     * @param TAG      日志标签，用于标识日志来源。
     * @param message  要打印的日志消息。
     */
    public static void w(String TAG, String message) {
        if (message != null) {
            android.util.Log.w(TAG, formatLog(message));
        }
    }

    /**
     * 格式化日志消息，添加当前的时间戳。
     *
     * @param message  要格式化的日志消息。
     * @return         格式化后的日志消息，包含时间戳。
     */
    @SuppressLint("DefaultLocale")
    private static String formatLog(String message) {
        if (message == null) {
            return "";
        }
        long timestamp = System.currentTimeMillis();
        return String.format("[%tF %<tT.%<tL] %s", timestamp, message);
    }
}