/*******************************************************************************
 * 文件名称: TEFModLoader
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

#include <TEFModLoader/TEFModLoader.hpp>
#include <TEFModLoader/utility.hpp>
#include <BNM/Loading.hpp>
#include <EFModLoader/loader.hpp>
#include <TEFModLoader/hook.hpp>
#include <TEFModLoader/manager.hpp>
#include <dlfcn.h>
#include <TEFModLoader/log.hpp>
#include <iostream>

void *EFModLoader::Loader::efopen(const char *p) { return dlopen(p, RTLD_LAZY); }
void *EFModLoader::Loader::efsym(void *h, const char *s) { return dlsym(h, s); }
int EFModLoader::Loader::efclose(void *h) { return dlclose(h); }

void TEFModLoader::initialize(JNIEnv *env) {
    std::cout << "Starting initialize function." << std::endl;

    jclass stateClass = env->FindClass("eternal/future/State");
    if (stateClass == nullptr) {
        std::cerr << "Failed to find State class: eternal/future/State" << std::endl;
        e_jni = false;
    } else {
        std::cout << "Successfully found State class: eternal/future/State" << std::endl;
        e_jni = true;
    }

    if (e_jni) {
        jfieldID modeFieldID = env->GetStaticFieldID(stateClass, "Mode", "I");
        if (modeFieldID == nullptr) {
            std::cerr << "Failed to find static field 'Mode' in State class." << std::endl;
            return;
        }
        std::cout << "Successfully found static field 'Mode' in State class." << std::endl;

        jfieldID efmodcFieldID = env->GetStaticFieldID(stateClass, "EFMod_c", "Ljava/lang/String;");
        if (efmodcFieldID == nullptr) {
            std::cerr << "Failed to find static field 'EFMod_c' in State class." << std::endl;
            return;
        }
        std::cout << "Successfully found static field 'EFMod_c' in State class." << std::endl;

        jint modeValue = env->GetStaticIntField(stateClass, modeFieldID);
        auto efmodcValue = (jstring) env->GetStaticObjectField(stateClass, efmodcFieldID);
        if (efmodcValue == nullptr) {
            std::cerr << "Failed to retrieve value for 'EFMod_c'." << std::endl;
            return;
        }
        std::cout << "Successfully retrieved values for Mode and EFMod_c." << std::endl;

        Mode = modeValue;
        const char* modDataPathCStr = env->GetStringUTFChars(efmodcValue, nullptr);
        modDataPath = std::string(modDataPathCStr);
        env->ReleaseStringUTFChars(efmodcValue, modDataPathCStr);

        std::cout << "Mode: " << Mode << ", modDataPath: " << modDataPath << std::endl;

        auxiliaryPath = Utility::getModDir().parent_path() / "libauxiliary.so";
        std::cout << "Auxiliary library path: " << auxiliaryPath << std::endl;
    }

    std::cout << "Loading mods from directory: " << Utility::getModDir() << " with data path: " << modDataPath << std::endl;
    EFModLoader::Loader::loadMods(Utility::getModDir(), modDataPath);
    std::cout << "Finished initialize function." << std::endl;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    TEFModLoader::Log::redirectStdStreams(); //重定向std::cout&std::cerr到安卓日志

    EFModAPI::getEFModAPI().registerAPI(
            ModApiDescriptor {
                    "",
                    "TEFModLoader",
                    "runtime",
                    "Mode",
                    "value",
                    0
                }.getID(),
                &TEFModLoader::Mode);

    EFModAPI::getEFModAPI().registerAPI(
            ModApiDescriptor {
                    "",
                    "TEFModLoader",
                    "runtime",
                    "printMemoryHexView",
                    "function",
                    3
            }.getID(),
            (void*)TEFModLoader::Utility::printMemoryHexView);

    TEFModLoader::initialize(env);
    BNM::Loading::TryLoadByJNI(env);
    BNM::Loading::AddOnLoadedEvent(TEFModLoader::Manager::API::autoProcessing);
    BNM::Loading::AddOnLoadedEvent(TEFModLoader::Hook::autoHook);
    BNM::Loading::AddOnLoadedEvent(EFModLoader::Loader::initiate);

    return JNI_VERSION_1_6;
}