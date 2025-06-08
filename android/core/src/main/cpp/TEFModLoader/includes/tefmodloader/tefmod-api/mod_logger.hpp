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

#pragma once

#include "logger_api.hpp"
#include "base_type_api.hpp"

#include "spdlog/spdlog.h"

namespace TEFModLoader::ModLogger {

    class Logger: TEFMod::Logger {
        std::string Tag; // 日志Tag
        std::string _filePath;  // 日志文件路径
        std::size_t _maxCache = 128 * 1024; // 最大缓存大小 (默认128kb)

        std::shared_ptr<spdlog::logger> _logger;

        void Log(Level level, const std::string& message) override;

    public:

        Logger(std::string Tag, std::string filePath, const std::size_t maxCache)
                : Tag(std::move(Tag)), _filePath(std::move(filePath)), _maxCache(maxCache) { }

        void init() override;

        static TEFMod::Logger* CreateLogger(const std::string& Tag, const std::string& filePath, const std::size_t maxCache);
    };

}