/*******************************************************************************
 * 文件名称: debug_tool
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

#include "debug_tool_api.hpp"

namespace TEFModLoader {

    class DebugTool: public TEFMod::DebugTool {
    public:
        void printMemoryHexView(TEFMod::Logger* logger, const void* ptr, size_t range, size_t hex_width) override;
        void printSystemInfo(TEFMod::Logger* logger) override;
        void printProfile(TEFMod::Logger* logger, std::function<void()>) override;
    };

}