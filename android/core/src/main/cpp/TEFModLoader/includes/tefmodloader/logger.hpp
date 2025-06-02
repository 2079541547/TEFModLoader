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

#pragma once

#include <spdlog/spdlog.h>
#include <string>

#define LOG_TRACE(...)    spdlog::trace(__VA_ARGS__)
#define LOG_DEBUG(...)    spdlog::debug(__VA_ARGS__)
#define LOG_INFO(...)     spdlog::info(__VA_ARGS__)
#define LOG_WARN(...)     spdlog::warn(__VA_ARGS__)
#define LOG_ERROR(...)    spdlog::error(__VA_ARGS__)
#define LOG_CRITICAL(...) spdlog::critical(__VA_ARGS__)

#define LOGF_TRACE(...)    spdlog::trace("[{}:{}:{}] {}", __FILE__, __LINE__, __FUNCTION__, fmt::format(__VA_ARGS__))
#define LOGF_DEBUG(...)    spdlog::debug("[{}:{}:{}] {}", __FILE__, __LINE__, __FUNCTION__, fmt::format(__VA_ARGS__))
#define LOGF_INFO(...)     spdlog::info("[{}:{}:{}] {}", __FILE__, __LINE__, __FUNCTION__, fmt::format(__VA_ARGS__))
#define LOGF_WARN(...)     spdlog::warn("[{}:{}:{}] {}", __FILE__, __LINE__, __FUNCTION__, fmt::format(__VA_ARGS__))
#define LOGF_ERROR(...)    spdlog::error("[{}:{}:{}] {}", __FILE__, __LINE__, __FUNCTION__, fmt::format(__VA_ARGS__))
#define LOGF_CRITICAL(...) spdlog::critical("[{}:{}:{}] {}", __FILE__, __LINE__, __FUNCTION__, fmt::format(__VA_ARGS__))

namespace TEFModLoader {

    class Logger {
    public:
        enum class Level {
            TRACE = spdlog::level::trace,
            DEBUG = spdlog::level::debug,
            INFO = spdlog::level::info,
            WARN = spdlog::level::warn,
            ERROR = spdlog::level::err,
            CRITICAL = spdlog::level::critical
        };

        static void Init(const std::string& logger_name = "TEFModLoader",
                         Level level = Level::INFO,
                         const std::string& filename = "",
                         bool console_output = true);

        static void SetLevel(Level level);
        static void Flush();
    };

}