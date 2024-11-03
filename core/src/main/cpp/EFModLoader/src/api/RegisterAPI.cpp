/*******************************************************************************
 * 文件名称: RegisterAPI
 * 项目名称: EFModLoader
 * 创建时间: 2024/9/28
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
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
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/


#include <EFModLoader/api/Redirect.hpp>
#include <EFModLoader/api/RegisterApi.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>


namespace EFModLoader::RegisterApi {
    vector<API> registerAPI;

    void RegisterAPI(const string& apiName, uintptr_t api_ptr) {
        // 查找已存在的API记录
        for (auto& api : registerAPI) {
            if (api.apiName == apiName) {
                EFLOG(LogLevel::WARN, "RegisterApi", "RegisterAPI", "API已存在：" + apiName + " 将不进行注册操作");
                return;
            }
        }
        // 如果没有找到，创建新的API记录
        registerAPI.push_back({apiName, api_ptr});
        EFLOG(LogLevel::INFO, "RegisterApi", "RegisterAPI", "注册了新的api：" + apiName);
    }

    void Register() {
        if (registerAPI.empty()) {
            EFLOG(LogLevel::WARN, "RegisterApi", "Register", "什么都没有诶٩(๑`^´๑)۶");
            return;
        }

        for (const auto& api : registerAPI) {
            if (EFModLoaderAPI::GetEFModLoader().FindAPIS(api.apiName).empty()) {
                EFLOG(LogLevel::WARN, "RegisterApi", "Register", "没有Mod注册的api：" + api.apiName);
            } else {
                for (auto a: EFModLoaderAPI::GetEFModLoader().FindAPIS(api.apiName)) {
                    Redirect::redirectPointer<void*>(a, api.new_ptr);
                }
                EFLOG(LogLevel::INFO, "RegisterApi", "Register", "已注册api：" + api.apiName);
            }
        }
        // 清空注册列表，防止重复注册
        registerAPI.clear();
    }

}