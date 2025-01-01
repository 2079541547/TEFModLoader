/*******************************************************************************
 * 文件名称: ModApi
 * 项目名称: EFModLoader
 * 创建时间: 2024/12/29
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
 
#include <EFMod/ModApi.hpp>
#include <Load/Loader.hpp>
#include <Manager/API.hpp>
#include <log.hpp>


void EFModLoader::ModApi::closeMod(std::string name, std::string author) {
        Load::unMod(std::hash<std::string>{}(name + author));
}

void EFModLoader::ModApi::modLog(std::string tag, int level,  std::string msg) {
        switch (level) {
                case 0:
                        EFLOG(DEBUG, tag, "Mod:\n", msg);
                        break;
                case 1:
                        EFLOG(INFO, tag, "Mod:\n", msg);
                        break;
                case 2:
                        EFLOG(ERROR, tag, "Mod:\n", msg);
                        break;
                case 3:
                        EFLOG(WARNING, tag, "Mod:\n", msg);
                        break;
        }
}

void EFModLoader::ModApi::initialize() {
        Manager::API::registerAPI({"","EFModLoader","EFMod","closeMod","void"}, (void*)closeMod);
        Manager::API::registerAPI({"","EFModLoader","EFMod","Log","void"}, (void*)modLog);
}