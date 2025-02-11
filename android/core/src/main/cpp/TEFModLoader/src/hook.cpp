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
    std::ostringstream uniqueName;
    uniqueName << "auxiliary_" << getpid() << "_" << nextIndex++ << ".so";
    std::filesystem::path uniquePath = std::filesystem::path(originalPath).parent_path() / uniqueName.str();

    if (!std::filesystem::exists(uniquePath)) {
        std::filesystem::copy_file(originalPath, uniquePath);
    }

    void* handle = dlopen(uniquePath.c_str(), RTLD_NOW | RTLD_LOCAL);
    if (!handle) {
        std::cerr << "Cannot open library: " << dlerror() << '\n';
        return nullptr;
    }

    loadedLibraries[uniquePath.string()] = std::shared_ptr<void>(handle, [](void* p) { /* 不关闭句柄 */ });

    if (std::filesystem::exists(uniquePath)) {
        std::filesystem::remove(uniquePath);
    }

    return handle;
}

typedef void (*createHookFunc)(int mode, int type, std::vector<void*> funPtrs, BNM::Class& c, BNM::MethodBase& method, size_t id, EFModAPI* efmodapi);
void TEFModLoader::Hook::autoHook() {
    for(const auto& funcDesc : EFModAPI::getEFModAPI().getFuncDescriptor()) {
        if (!funcDesc.File.empty()) {
            size_t dotPosition = funcDesc.Class.find('.');
            BNM::MethodBase* method;
            BNM::Class* Class;
            if (dotPosition != std::string::npos) {
                Class = new BNM::Class(BNM::Class(funcDesc.Namespace, funcDesc.Class.substr(0, dotPosition), BNM::Image(funcDesc.File)).GetInnerClass(funcDesc.Class.substr(dotPosition + 1)));
            } else {
                Class = new BNM::Class(BNM::Class(funcDesc.Namespace, funcDesc.Class, BNM::Image(funcDesc.File)));
            }
            method = new BNM::MethodBase(Class->GetMethod(funcDesc.Name, funcDesc.Arg));

            void* handle = SharedLibraryManager::getInstance().loadUniqueCopy(TEFModLoader::auxiliaryPath);
            if (!handle) {
                //EFLOG(ERROR, "自动创建hook", "加载共享库失败");
                continue;
            }

            auto createHook = (createHookFunc)dlsym(handle, "createHook");
            if (!createHook) {
                delete method;
                delete Class;
                //EFLOG(ERROR, "自动创建hook", "解析符号 'createHook' 失败");
                continue;
            }

            std::vector<void*> funPtrs{funcDesc.FunPtr};
            Type type;
            Mode hookMode;

            auto hookT = funcDesc.Type.substr(0, funcDesc.Type.find(">>"));
            auto hookFt = funcDesc.Type.substr(funcDesc.Type.find(">>") + 2);
            if (hookT == "hook") {
                hookMode = Mode::INLINE;
                // EFLOG(INFO, "自动创建hook", "内联Hook");
            } else if (hookT == "ihook") {
                hookMode = Mode::INVOKE;
                // EFLOG(INFO, "自动创建hook", "引擎Hook");
            } else if (hookT == "vhook"){
                hookMode = Mode::VIRTUAL;
                // EFLOG(INFO, "自动创建hook", "虚拟Hook");
            }

            if (hookFt == "void") {
                type = Type::VOID;
                // EFLOG(INFO, "自动创建hook", "void类型");
            } else if (hookFt == "int") {
                type = Type::INT;
                // EFLOG(INFO, "自动创建hook", "int类型");
            } else if (hookFt == "bool") {
                type = Type::BOOL;
                // EFLOG(INFO, "自动创建hook", "bool类型");
            }  else if (hookFt == "long") {
                type = Type::LONG;
                // EFLOG(INFO, "自动创建hook", "long类型");
            } else {
                delete method;
                delete Class;
                // EFLOG(ERROR, "自动创建hook", "未知类型:", hookFt);
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


            delete method;
            delete Class;

            // EFLOG(INFO, "自动创建hook", "HOOK创建成功:", funcDesc.getID());
        }
    }
}