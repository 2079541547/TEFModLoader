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
#include <iostream>

template<typename R, typename ...Args>
R EFModLoader::Utility::callFunction(void *funcPtr, Args &&...args) {
    std::cout << "Checking if function pointer is valid." << std::endl;

    if (!funcPtr) {
        std::cerr << "Error: The function pointer cannot be NULL" << std::endl;
        throw std::invalid_argument("The function pointer cannot be NULL");
    }

    std::cout << "Calling function with arguments:" << std::endl;

    using FuncPtr = R (*)(Args...);
    auto f = reinterpret_cast<FuncPtr>(funcPtr);

    R result = f(std::forward<Args>(args)...);

    std::cout << "Function returned value of type " << typeid(result).name() << std::endl;
    std::cout << "Function pointers " << funcPtr << std::endl;

    return result;
}

void EFModLoader::Utility::registerAPI(const ModApiDescriptor &api, void *ptr) {
    std::cout << "Starting to register API with ID: " << api.getID() << std::endl;

    if (ptr == nullptr) {
        std::cerr << "Error: Attempting to register API with a null pointer." << std::endl;
        return;
    }

    auto e = &EFModAPI::getEFModAPI();
    std::cout << "Retrieved EFModAPI instance for registration." << std::endl;

    e->registerAPI(api.getID(), ptr);
    std::cout << "Successfully registered API with ID: " << api.getID() << std::endl;
}