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

#include <log.hpp>
#include <utility>

const char *EFLog::levelToString(EFLog::LogLevel level)  {
        switch (level) {
                case LogLevel::DEBUG: return "DEBUG";
                case LogLevel::INFO: return "INFO";
                case LogLevel::WARNING: return "WARNING";
                case LogLevel::ERROR: return "ERROR";
                default: return "UNKNOWN";
        }
}

void EFLog::Logger::setOutput(std::function<void(const std::string &)> func) {
        outputFunc_ = std::move(func);
}

void EFLog::Logger::setCacheSize(size_t size)  {
        cacheSize_ = (size == -1) ? static_cast<size_t>(-1) : static_cast<size_t>(size);
}

void EFLog::Logger::setSourceCodeMode(bool enable) {
        sourceCodeMode_ = enable;
}

bool EFLog::Logger::isLoggingEnabled() const {
        return loggingEnabled_;
}

void EFLog::Logger::setLoggingEnabled(bool enable) {
        loggingEnabled_ = enable;
}

void EFLog::Logger::setAutoDumpToFile(bool enable)  {
        autoDumpToFile_ = enable;
}

void EFLog::Logger::setLogFile(const std::filesystem::path &filePath) {
        logFilePath_ = filePath;
        if (!exists(logFilePath_)) {
                std::ofstream fileStream(logFilePath_, std::ios_base::app);
                if (!fileStream.is_open()) {
                        throw std::runtime_error("Unable to open or create log file: " + logFilePath_.string());
                }
                fileStream.close();
        }
}

void EFLog::Logger::log(EFLog::LogLevel level, const std::string &identifier,
                        const std::string &formattedMessage, const char *file, int line,
                        const char *function) {
        if (!loggingEnabled_) return;
        
        // 创建日志条目并立即格式化
        std::ostringstream oss;
        oss << "[" << levelToString(level) << "] "
            << "[" << identifier << "] "
            << "[" << formatTime(std::chrono::system_clock::now()) << "] ";
        
        // 根据源码模式状态决定是否包含调用位置信息
        if (sourceCodeMode_ && file && function) {
                oss << "(" << file << ":" << line << " - " << function << ") ";
        }
        
        oss << formattedMessage;
        
        std::string logString = oss.str();
        
        // 立即输出日志
        if (outputFunc_) {
                outputFunc_(logString);
        }
        
        // 立即尝试转储日志到文件
        if (autoDumpToFile_) {
                dumpLogsToFile(logString);
        }
}

