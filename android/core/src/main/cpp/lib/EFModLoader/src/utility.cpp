/*******************************************************************************
 * 文件名称: utility
 * 项目名称: EFModLoader
 * 创建时间: 2025/2/11
 * 作者: EternalFuture
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: Licensed under the AGPLv3 License (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *
 *         http://www.gnu.org/licenses/agpl-3.0.html
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 *
 * 描述信息: 本文件为EFModLoader项目中的一部分，允许在遵守AGPLv3许可的条件下自由用于商业用途。
 * 注意事项: 请严格遵守AGPLv3协议使用本代码。AGPLv3要求您公开任何对原始软件的修改版本，并让这些修改也受到相同的许可证约束，即使是在通过网络交互的情况下。
 *******************************************************************************/

#include <EFModLoader/utility.hpp>

template<typename R, typename ...Args>
R EFModLoader::Utility::callFunction(void *funcPtr, Args &&...args) {
    if (!funcPtr) {
        throw std::invalid_argument("The function pointer cannot be NULL");
    }

    using FuncPtr = R (*)(Args...);
    auto f = reinterpret_cast<FuncPtr>(funcPtr);

    return f(std::forward<Args>(args)...);
}

void EFModLoader::Utility::registerAPI(const ModApiDescriptor &api, void *ptr) {
    auto e = &EFModAPI::getEFModAPI();
    e->registerAPI(api.getID(), ptr);
}