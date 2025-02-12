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

#include <TEFModLoader/hook.hpp>
#include <dlfcn.h>
#include <TEFModLoader/TEFModLoader.hpp>
#include <sstream>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Method.hpp>

TEFModLoader::Hook::SharedLibraryManager &TEFModLoader::Hook::SharedLibraryManager::getInstance() {
    static SharedLibraryManager instance;
    return instance;
}

void *TEFModLoader::Hook::SharedLibraryManager::loadUniqueCopy(const std::string &originalPath) {
    std::cout << "Starting loadUniqueCopy with original path: " << originalPath << std::endl;

    std::ostringstream uniqueName;
    uniqueName << "auxiliary_" << getpid() << "_" << nextIndex++ << ".so";
    std::filesystem::path uniquePath = std::filesystem::path(originalPath).parent_path() / uniqueName.str();

    std::cout << "Generated unique library path: " << uniquePath << std::endl;

    if (!std::filesystem::exists(uniquePath)) {
        std::cout << "Attempting to copy file from " << originalPath << " to " << uniquePath << std::endl;
        std::filesystem::copy_file(originalPath, uniquePath);
        std::cout << "File copied successfully." << std::endl;
    } else {
        std::cout << "File already exists at " << uniquePath << std::endl;
    }

    void* handle = dlopen(uniquePath.c_str(), RTLD_NOW | RTLD_LOCAL);
    if (!handle) {
        std::cerr << "Cannot open library: " << dlerror() << '\n';
        return nullptr;
    }
    std::cout << "Library opened successfully: " << uniquePath << std::endl;

    loadedLibraries[uniquePath.string()] = std::shared_ptr<void>(handle, [](void* p) { /* 不关闭句柄 */ });

    if (std::filesystem::exists(uniquePath)) {
        std::cout << "Attempting to remove temporary library file: " << uniquePath << std::endl;
        std::filesystem::remove(uniquePath);
        std::cout << "Temporary library file removed." << std::endl;
    } else {
        std::cout << "No temporary library file found for removal at: " << uniquePath << std::endl;
    }

    return handle;
}

typedef void (*createHookFunc)(int mode, int type, std::vector<void*> funPtrs, BNM::Class& c, BNM::MethodBase& method, size_t id, EFModAPI* efmodapi);
void TEFModLoader::Hook::autoHook() {
    std::cout << "Starting autoHook process." << std::endl;

    for(const auto& funcDesc : EFModAPI::getEFModAPI().getFuncDescriptor()) {
        if (!funcDesc.File.empty()) {
            std::cout << "Processing function descriptor with File: " << funcDesc.File
                      << ", Namespace: " << funcDesc.Namespace
                      << ", Class: " << funcDesc.Class
                      << ", Name: " << funcDesc.Name << std::endl;

            size_t dotPosition = funcDesc.Class.find('.');
            BNM::MethodBase* method;
            BNM::Class* Class;
            if (dotPosition != std::string::npos) {
                std::cout << "Class name contains '.', processing inner class." << std::endl;
                Class = new BNM::Class(BNM::Class(funcDesc.Namespace, funcDesc.Class.substr(0, dotPosition), BNM::Image(funcDesc.File)).GetInnerClass(funcDesc.Class.substr(dotPosition + 1)));
            } else {
                std::cout << "Class name does not contain '.', processing regular class." << std::endl;
                Class = new BNM::Class(BNM::Class(funcDesc.Namespace, funcDesc.Class, BNM::Image(funcDesc.File)));
            }

            method = new BNM::MethodBase(Class->GetMethod(funcDesc.Name, funcDesc.Arg));
            std::cout << "Method retrieved successfully." << std::endl;

            void* handle = SharedLibraryManager::getInstance().loadUniqueCopy(TEFModLoader::auxiliaryPath);
            if (!handle) {
                std::cerr << "Failed to load shared library." << std::endl;
                delete method;
                delete Class;
                continue;
            }
            std::cout << "Shared library loaded successfully." << std::endl;

            auto createHook = (createHookFunc)dlsym(handle, "createHook");
            if (!createHook) {
                std::cerr << "Failed to resolve symbol 'createHook'." << std::endl;
                delete method;
                delete Class;
                continue;
            }
            std::cout << "Symbol 'createHook' resolved successfully." << std::endl;

            std::vector<void*> funPtrs{funcDesc.FunPtr};
            Type type;
            Mode hookMode;

            auto hookT = funcDesc.Type.substr(0, funcDesc.Type.find(">>"));
            auto hookFt = funcDesc.Type.substr(funcDesc.Type.find(">>") + 2);

            if (hookT == "hook") {
                hookMode = Mode::INLINE;
                std::cout << "Setting hook mode to INLINE." << std::endl;
            } else if (hookT == "ihook") {
                hookMode = Mode::INVOKE;
                std::cout << "Setting hook mode to INVOKE." << std::endl;
            } else if (hookT == "vhook"){
                hookMode = Mode::VIRTUAL;
                std::cout << "Setting hook mode to VIRTUAL." << std::endl;
            }

            if (hookFt == "void") {
                type = Type::VOID;
                std::cout << "Setting return type to VOID." << std::endl;
            } else if (hookFt == "int") {
                type = Type::INT;
                std::cout << "Setting return type to INT." << std::endl;
            } else if (hookFt == "bool") {
                type = Type::BOOL;
                std::cout << "Setting return type to BOOL." << std::endl;
            } else if (hookFt == "long") {
                type = Type::LONG;
                std::cout << "Setting return type to LONG." << std::endl;
            } else {
                std::cerr << "Unknown return type: " << hookFt << std::endl;
                delete method;
                delete Class;
                continue;
            }

            createHook(static_cast<int>(hookMode),
                       static_cast<int>(type),
                       funPtrs,
                       *Class,
                       *method,
                       ModApiDescriptor{
                               funcDesc.File,
                               funcDesc.Namespace,
                               funcDesc.Class,
                               funcDesc.Name,
                               "old_fun"
                       }.getID(),
                       &EFModAPI::getEFModAPI());

            std::cout << "Hook created successfully for function descriptor." << std::endl;

            delete method;
            delete Class;
        }
    }
    std::cout << "Finished autoHook process." << std::endl;
}