/*******************************************************************************
 * 文件名称: log
 * 项目名称: EFModLoader
 * 创建时间: 2024/9/28
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

#ifndef EFMODLOADER_LOG_HPP
#define EFMODLOADER_LOG_HPP

#include <android/log.h>
#include <chrono>
#include <sstream>
#include <string>
#include <thread>
#include <unistd.h> // For getpid

namespace EFModLoader {

    enum class LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    };

    class Log {
    public:
        // 获取当前时间的字符串表示
        static std::string getCurrentTime();

        // 内部日志记录方法
        static void logInternal(LogLevel level, const std::string &fullMessage);

        // 日志记录方法
        static void LOG(LogLevel level, const std::string &message, const char* file, int line);
        static void LOG(LogLevel level, const std::string &function, const std::string &message, const char* file, int line);
        static void LOG(LogLevel level, const std::string &Class, const std::string &function, const std::string &message, const char* file, int line);
        static void LOG(LogLevel level, const std::string &Namespace, const std::string &Class, const std::string &function, const std::string &message, const char* file, int line);

    private:
        // 将日志级别转换为字符串
        static std::string logLevelToString(LogLevel level);
    };

} // namespace EFModLoader

// 宏定义，用于捕获文件名和行号
#define EFLOG(level, ...) EFModLoader::Log::LOG(level, ##__VA_ARGS__, __FILE__, __LINE__)

#endif // EFMODLOADER_LOG_HPP