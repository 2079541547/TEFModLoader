/*******************************************************************************
 * 文件名称: logger
 * 项目名称: EFMod
 * 创建时间: 25-5-4
 * 作者: EternalFuture゙
 * Gitlab: https://gitlab.com/2079541547/
 * 协议: MIT
 *
 * MIT License
 *
 * Copyright (c) 2025 EternalFuture゙
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/

#pragma once

#include <chrono>
#include <functional>
#include <mutex>
#include <sstream>
#include <string>
#include <thread>

namespace EFModLoader::Log {
    // 日志级别枚举
    enum class Level {
        Trace, // 最详细的日志信息
        Debug, // 调试信息
        Info, // 一般信息
        Warning, // 警告信息
        Error, // 错误信息
        Critical // 严重错误信息
    };

    // 日志记录结构体
    struct Record {
        std::chrono::system_clock::time_point time; // 日志时间
        Level level; // 日志级别
        std::string message; // 日志消息
        std::string sourceFile; // 源文件名
        unsigned int sourceLine; // 源代码行号
        std::string sourceFunction; // 源函数名
        std::thread::id threadId; // 线程ID
    };

    using LogOutputFunc = std::function<void(const Record &)>;

    class Logger {
    public:
        static void SetOutputFunction(LogOutputFunc outputFunc);

        static void SetMinLevel(Level level);

        static Level GetMinLevel();

        template<typename... Args>
        static void Log(Level level,
                        const char *file,
                        unsigned int line,
                        const char *function,
                        Args &&... args);

    private:
        inline static std::mutex mutex_;
        inline static LogOutputFunc outputFunc_;
        inline static Level minLevel_;
    };

    template<typename... Args>
    void Logger::Log(const Level level, const char *file, const unsigned int line, const char *function,
                     Args &&... args) {
        if (level < minLevel_) {
            return;
        }

        std::ostringstream oss;
        (oss << ... << std::forward<Args>(args));

        const Record record{
            std::chrono::system_clock::now(),
            level,
            oss.str(),
            file ? file : "",
            line,
            function ? function : "",
            std::this_thread::get_id()
        };

        std::lock_guard lock(mutex_);
        if (outputFunc_) {
            outputFunc_(record);
        }
    }

    const char *LevelToString(Level level);

#ifdef EFMODLAODER_ENABLE_LOGGING
#define LOG_TRACE(...)   EFModLoader::Log::Logger::Log(EFModLoader::Log::Level::Trace,   __FILE__, __LINE__, __func__, __VA_ARGS__)
#define LOG_DEBUG(...)   EFModLoader::Log::Logger::Log(EFModLoader::Log::Level::Debug,   __FILE__, __LINE__, __func__, __VA_ARGS__)
#define LOG_INFO(...)    EFModLoader::Log::Logger::Log(EFModLoader::Log::Level::Info,    __FILE__, __LINE__, __func__, __VA_ARGS__)
#define LOG_WARN(...)    EFModLoader::Log::Logger::Log(EFModLoader::Log::Level::Warning, __FILE__, __LINE__, __func__, __VA_ARGS__)
#define LOG_ERROR(...)   EFModLoader::Log::Logger::Log(EFModLoader::Log::Level::Error,   __FILE__, __LINE__, __func__, __VA_ARGS__)
#define LOG_CRITICAL(...) EFModLoader::Log::Logger::Log(EFModLoader::Log::Level::Critical, __FILE__, __LINE__, __func__, __VA_ARGS__)
#else
#define LOG_TRACE(...)   (void)0
#define LOG_DEBUG(...)   (void)0
#define LOG_INFO(...)    (void)0
#define LOG_WARN(...)    (void)0
#define LOG_ERROR(...)   (void)0
#define LOG_CRITICAL(...) (void)0
#endif
}
