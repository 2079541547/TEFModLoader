/*******************************************************************************
 * 文件名称: mod_logger
 * 项目名称: TEFModLoader
 * 创建时间: 2025/5/16
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
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#include "tefmod-api/mod_logger.hpp"

#include "spdlog/sinks/rotating_file_sink.h"
#include "spdlog/sinks/android_sink.h"

#include "tefmod-api/base_type.hpp"

#include <iomanip>
#include <ctime>
#include <chrono>

void TEFModLoader::ModLogger::Logger::init() {
    try {
        std::vector<spdlog::sink_ptr> sinks;

        auto android_sink = std::make_shared<spdlog::sinks::android_sink_mt>(Tag);
        android_sink->set_level(spdlog::level::trace);
        sinks.push_back(android_sink);

        if (!_filePath.empty()) {
            auto file_sink = std::make_shared<spdlog::sinks::rotating_file_sink_mt>(
                    _filePath,
                    _maxCache, // 单个文件最大5MB
                    1,               // 保留3个备份文件
                    true            // 自动刷新
            );
            file_sink->set_level(spdlog::level::trace);
            sinks.push_back(file_sink);
        }

        _logger = std::make_shared<spdlog::logger>(Tag, begin(sinks), end(sinks));
        _logger->set_level(spdlog::level::trace);

        // 设置日志格式
        _logger->set_pattern("[%Y-%m-%d %H:%M:%S.%e] [%^%l%$] [%n] %v");

        spdlog::register_logger(_logger);

        // 设置刷新级别
        _logger->flush_on(spdlog::level::warn);

        // 设置自动刷新周期
        spdlog::flush_every(std::chrono::seconds(3));

    } catch (const spdlog::spdlog_ex& ex) {
        spdlog::error("Logger initialization failed for {}: {}", Tag, ex.what());
        throw;
    }
}

void TEFModLoader::ModLogger::Logger::Log(Level level, const std::string& message) {
    if (!_logger) return;

    // 调用 spdlog
    switch (level) {
        case Level::Trace:    _logger->trace(message);    break;
        case Level::Debug:    _logger->debug(message);    break;
        case Level::Info:     _logger->info(message);     break;
        case Level::Warning:  _logger->warn(message);    break;
        case Level::Error:    _logger->error(message);   break;
        case Level::Critical: _logger->critical(message); break;
    }
}


TEFMod::Logger *TEFModLoader::ModLogger::Logger::CreateLogger(const std::string& Tag, const std::string& filePath, const std::size_t maxCache) {
    return new Logger(Tag, filePath, maxCache);
}