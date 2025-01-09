/*******************************************************************************
 * 文件名称: Loader
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

#include <cstdint>
#include <vector>
#include <unordered_set>
#include <mutex>

#include <Load/load.hpp>
#include <EFMod/EFMod.hpp>

namespace EFModLoader::Load {
    struct Mod {
        size_t id;
        void* loaded;
        EFMod* Instance;
        std::filesystem::path loadPath;
        std::filesystem::path modData;
        ModMetadata info;
    };

    inline std::vector<Mod> mod{};

    void loadMod(const std::filesystem::path& Path);
    void loadAFile(const std::filesystem::path& Path);
    void addLoadedMod(Mod&& newMod, std::mutex& mtx);
    bool isModIDLoaded(const std::unordered_set<size_t>& loadedModIDs, size_t modID, std::mutex& mtx);
    void loadSingleMod(const std::filesystem::path& entry, std::unordered_set<size_t>& loadedModIDs, std::mutex& modsMtx);
    void loadModsAsync(const std::filesystem::path& rootDir);
    void initiate();
    void unMod(const size_t& id);
}
