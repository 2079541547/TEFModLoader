/*******************************************************************************
 * 文件名称: logger
 * 项目名称: TEFModLoader
 * 创建时间: 2025/5/17
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

#include <logger.hpp>

#include <spdlog/sinks/android_sink.h>
#include <spdlog/sinks/basic_file_sink.h>
#include <memory>

void TEFModLoader::Logger::Init(const std::string& logger_name, Level level, const std::string& filename, bool console_output) {
    try {
        auto existing_logger = spdlog::get(logger_name);
        if (existing_logger) {
            return;
        }

        std::vector<spdlog::sink_ptr> sinks;

        if (console_output) {
            auto android_sink = std::make_shared<spdlog::sinks::android_sink_mt>("TEFModLoader");
            android_sink->set_pattern("%^[%Y-%m-%d %H:%M:%S.%e] [%l] [%n] %v%$");
            sinks.push_back(android_sink);
        }

        if (!filename.empty()) {
            auto file_sink = std::make_shared<spdlog::sinks::basic_file_sink_mt>(filename, true);
            file_sink->set_pattern("[%Y-%m-%d %H:%M:%S.%e] [%l] [%n] [%t] %v");
            sinks.push_back(file_sink);
        }

        auto logger = std::make_shared<spdlog::logger>(logger_name, begin(sinks), end(sinks));
        logger->set_level(static_cast<spdlog::level::level_enum>(level));

        if (!spdlog::get(logger_name)) {
            spdlog::register_logger(logger);
            spdlog::set_default_logger(logger);
        } else {
            logger = spdlog::get(logger_name);
        }

        logger->flush_on(static_cast<spdlog::level::level_enum>(Level::WARN));

        LOG_INFO("New logger initialized successfully. Level: {}", static_cast<int>(level));

    } catch (const spdlog::spdlog_ex& ex) {
        spdlog::error("Logger initialization failed: {}", ex.what());
        throw;
    }
}

void TEFModLoader::Logger::SetLevel(Level level) {
    spdlog::set_level(static_cast<spdlog::level::level_enum>(level));
}

void TEFModLoader::Logger::Flush() {
    spdlog::default_logger()->flush();
}