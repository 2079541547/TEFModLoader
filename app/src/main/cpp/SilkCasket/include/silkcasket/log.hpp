/*******************************************************************************
 * 文件名称: log
 * 项目名称: SilkCasket
 * 创建时间: 2024/11/24
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547 
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 *
 * 描述信息: 本文件为SilkCasket项目中的一部分，允许在遵守Apache License 2.0的条件下自由用于商业用途。
 * 注意事项: 请严格遵守Apache License 2.0协议使用本代码。Apache License 2.0允许商业用途，无需额外授权。
 *******************************************************************************/

#pragma once

#include <chrono>
#include <sstream>
#include <string>
#include <thread>
#include <unistd.h>


#ifdef ANDROID_ENABLE
#include <android/log.h>
#endif



namespace SilkCasket {

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


    inline std::string Log::getCurrentTime() {
        auto now = std::chrono::system_clock::now();
        auto in_time_t = std::chrono::system_clock::to_time_t(now);

        std::tm buffer;
        localtime_r(&in_time_t, &buffer);

        char timeStr[20];
        strftime(timeStr, sizeof(timeStr), "%Y-%m-%d %X", &buffer);

        return std::string(timeStr);
    }

    inline std::string Log::logLevelToString(LogLevel level) {
        switch (level) {
            case LogLevel::ERROR: return "ERROR";
            case LogLevel::WARN: return "WARN";
            case LogLevel::INFO: return "INFO";
            case LogLevel::DEBUG: return "DEBUG";
            case LogLevel::TRACE: return "TRACE";
        }
        return "UNKNOWN";
    }

    inline void Log::logInternal(LogLevel level, const std::string &fullMessage) {

#ifdef ENABLE_LOG

#ifdef ANDROID_ENABLE
        switch (level) {
            case LogLevel::ERROR:
                __android_log_print(ANDROID_LOG_ERROR, "SilkCasket", "%s", fullMessage.c_str());
                break;
            case LogLevel::WARN:
                __android_log_print(ANDROID_LOG_WARN, "SilkCasket", "%s", fullMessage.c_str());
                break;

            case LogLevel::INFO:
                __android_log_print(ANDROID_LOG_INFO, "SilkCasket", "%s", fullMessage.c_str());
                break;
            case LogLevel::DEBUG:
                __android_log_print(ANDROID_LOG_DEBUG, "SilkCasket", "%s", fullMessage.c_str());
                break;
            case LogLevel::TRACE:
                __android_log_print(ANDROID_LOG_DEFAULT, "SilkCasket", "%s", fullMessage.c_str());
                break;
        }
#else

        switch (level) {
        case LogLevel::ERROR:
            std::cerr << fullMessage << std::endl;
            break;
        case LogLevel::WARN:
            std::cerr << fullMessage << std::endl;
            break;
        case LogLevel::INFO:
            std::cout << fullMessage << std::endl;
            break;
        case LogLevel::DEBUG:
            std::cout << fullMessage << std::endl;
            break;
        case LogLevel::TRACE:
            std::cout  << fullMessage << std::endl;
            break;
    }
#endif

#else
#endif

    }

    inline void Log::LOG(LogLevel level, const std::string &message, const char* file, int line) {
        std::string fullMessage = "[" + getCurrentTime() + "] [" + std::to_string(getpid()) + "] [" + logLevelToString(level) + "] " + " [" + file + ":" + std::to_string(line) + "] " + message;
        logInternal(level, fullMessage);
    }

    inline void Log::LOG(LogLevel level, const std::string &function, const std::string &message, const char* file, int line) {
        std::string position = "[" + function + "]";
        std::string fullMessage = "[" + getCurrentTime() + "] [" + std::to_string(getpid()) + "] [" + logLevelToString(level) + "] " + position + " " + "[" + file + ":" + std::to_string(line) + "] " +  message;
        logInternal(level, fullMessage);
    }

    inline void Log::LOG(LogLevel level, const std::string &Class, const std::string &function, const std::string &message, const char* file, int line) {
        std::string position = "[" + Class + "::" + function + "]";
        std::string fullMessage = "[" + getCurrentTime() + "] [" + std::to_string(getpid()) + "] [" + logLevelToString(level) + "] " + position + " " + "[" + file + ":" + std::to_string(line) + "] " + message;
        logInternal(level, fullMessage);
    }

    inline void Log::LOG(LogLevel level, const std::string &Namespace, const std::string &Class, const std::string &function, const std::string &message, const char* file, int line) {
        std::string position = "[" + Namespace + "::" + Class + "::" + function + "]";
        std::string fullMessage = "[" + getCurrentTime() + "] [" + std::to_string(getpid()) + "] [" + logLevelToString(level) + "] " + position + " " + "[" + file + ":" + std::to_string(line) + "] " + message;
        logInternal(level, fullMessage);
    }
}

#define LOG(level, ...) SilkCasket::Log::LOG(level, ##__VA_ARGS__, __FILE__, __LINE__)
