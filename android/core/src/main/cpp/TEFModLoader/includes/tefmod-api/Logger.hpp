/*******************************************************************************
 * 文件名称: Logger
 * 项目名称: TEFMod-API
 * 创建时间: 25-5-11
 * 作者: EternalFuture゙
 * Gitlab: https://gitlab.com/2079541547/
 * 协议: Apache License 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
 
#pragma once

#include <string>
#include <sstream>

namespace TEFMod {

    class Logger {
    public:

        virtual ~Logger() = default;

        enum class Level {
            Trace, // 最详细的日志信息
            Debug, // 调试信息
            Info, // 一般信息
            Warning, // 警告信息
            Error, // 错误信息
            Critical // 严重错误信息
        };

        virtual void init() = 0;


        template<typename... Args>
        void t(Args &&... args) { LogForward(Level::Trace, std::forward<Args>(args)...); }

        template<typename... Args>
        void d(Args &&... args) { LogForward(Level::Debug, std::forward<Args>(args)...); }

        template<typename... Args>
        void i(Args &&... args) { LogForward(Level::Info, std::forward<Args>(args)...); }

        template<typename... Args>
        void w(Args &&... args) { LogForward(Level::Warning, std::forward<Args>(args)...); }

        template<typename... Args>
        void e(Args &&... args) { LogForward(Level::Error, std::forward<Args>(args)...); }

        template<typename... Args>
        void c(Args &&... args) { LogForward(Level::Critical, std::forward<Args>(args)...); }

    private:
        static std::string LevelToString(const Level level) {
            switch (level) {
                case Level::Trace: return "TRACE";
                case Level::Debug: return "DEBUG";
                case Level::Info: return "INFO";
                case Level::Warning: return "WARN";
                case Level::Error: return "ERROR";
                case Level::Critical: return "CRITICAL";
                default: return "UNKNOWN";
            }
        }


        virtual void Log(Level level, const std::string& message) = 0;

        template<typename... Args>
        void LogForward(const Level level, Args &&... args) {
            std::ostringstream oss;
            if constexpr (sizeof...(args) > 0) { // 防止空包报错
                (oss << ... << std::forward<Args>(args)); // C++17 折叠表达式
            }
            Log(level, oss.str());
        }
    };

}
