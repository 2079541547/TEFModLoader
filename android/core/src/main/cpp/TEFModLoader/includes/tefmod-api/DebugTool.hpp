/*******************************************************************************
 * 文件名称: DebugTool
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

#include <functional>

#include "Logger.hpp"

namespace TEFMod {
    class DebugTool {
    public:
        virtual ~DebugTool() = default;

        virtual void printMemoryHexView(Logger* logger, const void* ptr, size_t range, size_t hex_width) = 0;
        virtual void printSystemInfo(Logger* logger) = 0;
        virtual void printProfile(Logger* logger, std::function<void()> func) = 0;
    };
}