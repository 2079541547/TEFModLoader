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

#include <EFModLoader/api/RegisterApi.hpp>

namespace EFModLoader::RegisterApi {

    // 存储所有待注册的API信息
    std::vector<API> registerAPI;

    /**
     * 注册一个API到系统中。
     *
     * @param apiName API的名称。
     * @param api_ptr API函数指针。
     */
    void RegisterAPI(const std::string& apiName, uintptr_t api_ptr) {
        // 检查是否已经存在相同名称的API
        for (auto& api : registerAPI) {
            if (api.apiName == apiName) {
                // 如果API已存在，记录警告日志并返回
                EFLOG(LogLevel::WARN, "RegisterApi", "RegisterAPI", "API已存在：" + apiName + " 将不进行注册操作");
                return;
            }
        }

        // 添加新的API到注册列表中
        registerAPI.push_back({apiName, api_ptr});
        // 记录成功注册API的日志
        EFLOG(LogLevel::INFO, "RegisterApi", "RegisterAPI", "成功注册新API：" + apiName);
    }

    /**
     * 执行所有已注册API的实际注册过程。
     */
    void Register() {
        // 检查注册列表是否为空
        if (registerAPI.empty()) {
            // 如果注册列表为空，记录警告日志并返回
            EFLOG(LogLevel::WARN, "RegisterApi", "Register", "注册列表为空，无API需要注册。");
            return;
        }

        // 遍历所有待注册的API
        for (const auto& api : registerAPI) {
            // 尝试从EFModLoader中查找API
            auto foundApis = EFModLoaderAPI::GetEFModLoader().FindAPIS(api.apiName);
            if (foundApis.empty()) {
                // 如果未找到匹配的API，记录错误日志并跳过该API
                EFLOG(LogLevel::ERROR, "RegisterApi", "Register", "未找到与API名称匹配的Mod API：" + api.apiName);
                continue;
            }

            // 对于每个找到的API地址，重定向到新的API实现
            for (auto a : foundApis) {
                try {
                    // 重定向API指针
                    Redirect::redirectPointer<void*>(a, api.new_ptr);
                    // 记录成功重定向API的日志
                    EFLOG(LogLevel::INFO, "RegisterApi", "Register", "成功将API：" + api.apiName + " 重定向到新实现。");
                } catch (const std::exception& e) {
                    // 如果重定向过程中发生错误，记录错误日志
                    EFLOG(LogLevel::ERROR, "RegisterApi", "Register", "重定向API：" + api.apiName + " 时发生错误：" + std::string(e.what()));
                }
            }
        }

        // 清空注册列表以备下次注册
        registerAPI.clear();
    }

}