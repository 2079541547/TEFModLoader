/*******************************************************************************
 * 文件名称: log
 * 项目名称: EFModLoader
 * 创建时间: 2024/12/28
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547 
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
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
 *
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#pragma once

#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <filesystem>
#include <functional>
#include <queue>
#include <cassert>
#include <chrono>
#include <iomanip>

namespace EFLog {
    
    // 日志级别枚举
    enum class LogLevel {
        DEBUG,
        INFO,
        WARNING,
        ERROR
    };
    
    // 将日志级别转换为字符串
    const char* levelToString(LogLevel level);
    
    // 单条日志条目结构体
    struct LogEntry {
        LogLevel level;
        std::string identifier; // 模块标识
        std::string message;
        std::string file;
        int line;
        std::string function;
        std::chrono::system_clock::time_point timestamp;
        
        // 构造函数
        LogEntry(LogLevel lvl, const std::string& id, const std::string& msg,
                 const std::string& f = "", int l = 0, const std::string& func = "")
                : level(lvl), identifier(id), message(msg), file(f), line(l), function(func),
                  timestamp(std::chrono::system_clock::now()) {}
    };
    
    // 日志系统类
    class Logger {
    public:
        // 设置日志输出目标
        void setOutput(std::function<void(const std::string&)> func);
        
        // 设置缓存大小，-1表示无限制
        void setCacheSize(size_t size);
        
        // 启用或禁用源码模式
        void setSourceCodeMode(bool enable);
        
        // 启用或禁用日志记录
        bool isLoggingEnabled() const;
        
        void setLoggingEnabled(bool enable);
        
        // 启用或禁用自动转储到文件
        void setAutoDumpToFile(bool enable);
        
        // 设置日志文件路径
        void setLogFile(const std::filesystem::path& filePath) ;
        
        // 打印日志，带有格式化支持
        void log(LogLevel level, const std::string& identifier, const std::string& formattedMessage,
                 const char* file = "", int line = 0, const char* function = "");
        
        // 公共静态方法用于格式化字符串
        template<typename... Args>
        static std::string formatString(const char* format, Args... args) {
                std::ostringstream oss;
                oss << format;
                ((oss << " " << args), ...); // 使用 C++17 fold expression
                return oss.str();
        }
    
    private:
        // 格式化时间戳
        inline std::string formatTime(const std::chrono::system_clock::time_point& tp) {
                auto in_time_t = std::chrono::system_clock::to_time_t(tp);
                std::tm buf;
#if defined(_WIN32)
                localtime_s(&buf, &in_time_t);
#else
                localtime_r(&in_time_t, &buf);
#endif
                char timeStr[20];
                strftime(timeStr, sizeof(timeStr), "%Y-%m-%d %H:%M:%S", &buf);
                return std::string(timeStr);
        }
        
        inline bool checkAndHandleFileSize() {
                if (cacheSize_ == 0 || logFilePath_.empty()) {
                        return true; // 不限制文件大小或路径未设置
                }
                
                try {
                        std::error_code ec;
                        auto fileSize = std::filesystem::file_size(logFilePath_, ec);
                        if (ec) {
                                std::cerr << "Error getting file size: " << ec.message() << std::endl;
                                return false;
                        }
                        
                        if (fileSize >= cacheSize_) {
                                // 调用reduceLogFileToSize并传入目标大小
                                reduceLogFileToSize(cacheSize_);
                                return true;
                        }
                } catch (const std::filesystem::filesystem_error& e) {
                        std::cerr << "Filesystem error: " << e.what() << std::endl;
                        return false;
                }
                
                return true;
        }
        
        // 减少日志文件大小到目标大小，通过移除最老的日志条目
        inline void reduceLogFileToSize(size_t targetSize) {
                if (logFilePath_.empty()) {
                        return;
                }
                
                try {
                        // 打开文件进行读取
                        std::ifstream logFile(logFilePath_);
                        if (!logFile.is_open()) {
                                std::cerr << "Failed to open log file for reading: " << logFilePath_ << std::endl;
                                return;
                        }
                        
                        std::vector<std::string> lines;
                        std::string line;
                        while (std::getline(logFile, line)) {
                                lines.push_back(line);
                        }
                        logFile.close();
                        
                        // 如果文件为空，则直接返回
                        if (lines.empty()) {
                                return;
                        }
                        
                        // 计算当前文件大小
                        size_t currentSize = 0;
                        for (const auto& l : lines) {
                                currentSize += l.size() + 1; // 每行加上换行符的长度
                        }
                        
                        // 如果文件已经小于或等于目标大小，无需处理
                        if (currentSize <= targetSize) {
                                return;
                        }
                        
                        // 删除最早的行直到文件大小小于等于目标大小
                        while (!lines.empty() && currentSize > targetSize) {
                                currentSize -= lines.front().size() + 1; // 减去换行符的长度
                                lines.erase(lines.begin());
                        }
                        
                        // 将剩余的行写回文件
                        std::ofstream outFile(logFilePath_, std::ios::trunc); // 使用trunc标志清除文件内容
                        if (!outFile.is_open()) {
                                std::cerr << "Failed to open log file for writing: " << logFilePath_ << std::endl;
                                return;
                        }
                        for (const auto& l : lines) {
                                outFile << l << "\n";
                        }
                        outFile.close();
                } catch (const std::filesystem::filesystem_error& e) {
                        std::cerr << "Filesystem error in reduceLogFileToSize: " << e.what() << std::endl;
                }
        }
        
        // 转存单个日志条目到文件
        inline void dumpLogsToFile(const std::string& logMessage) {
                if (!checkAndHandleFileSize()) {
                        return;
                }
                
                if (logFilePath_.empty()) {
                        std::cerr << "Log file path not set." << std::endl;
                        return;
                }
                
                std::ofstream logFile(logFilePath_, std::ios::app | std::ios::out); // 追加模式打开文件
                if (!logFile.is_open()) {
                        std::cerr << "Failed to open log file: " << logFilePath_ << std::endl;
                        return;
                }
                
                logFile << logMessage << std::endl;
                logFile.close();
        }
        
        size_t cacheSize_; // 缓存大小，-1表示无限制
        std::function<void(const std::string&)> outputFunc_;
        std::queue<LogEntry> logQueue_;
        bool sourceCodeMode_; // 是否启用源码模式
        bool loggingEnabled_; // 是否启用日志记录
        bool autoDumpToFile_; // 是否启用自动转储到文件
        std::filesystem::path logFilePath_; // 日志文件路径
    };
    
}

inline EFLog::Logger logger;
#define EFLOG(level, identifier, ...) \
    do { \
        if (logger.isLoggingEnabled()) { \
            logger.log(EFLog::LogLevel::level, identifier, EFLog::Logger::formatString(__VA_ARGS__), \
                       __FILE__, __LINE__, __FUNCTION__); \
        } \
    } while(0)
