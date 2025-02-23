/*******************************************************************************
 * 文件名称: hook
 * 项目名称: TEFModLoader
 * 创建时间: 2025/2/11
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

#include <filesystem>
#include <string>
#include <unistd.h>
#include <iostream>

namespace TEFModLoader::Hook {

    enum Type {
        LONG = 0,
        INT = 1,
        VOID = 2,
        BOOL = 3,
        STRING = 4
    };

    enum Mode {
        INLINE = 0,
        VIRTUAL = 1,
        INVOKE = 2
    };


    class SharedLibraryManager {
    public:
        static SharedLibraryManager& getInstance();

        void* loadUniqueCopy(const std::string& originalPath);

    private:
        SharedLibraryManager() = default;
        ~SharedLibraryManager() = default;
        std::unordered_map<std::string, std::shared_ptr<void>> loadedLibraries;
        int nextIndex;
        SharedLibraryManager(const SharedLibraryManager&) = delete;
        SharedLibraryManager& operator=(const SharedLibraryManager&) = delete;
    };

    void autoHook();

}