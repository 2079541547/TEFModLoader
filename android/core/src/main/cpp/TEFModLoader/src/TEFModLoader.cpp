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

void *EFModLoader::Loader::efopen(const char *p) { return dlopen(p, RTLD_LAZY); }
void *EFModLoader::Loader::efsym(void *h, const char *s) { return dlsym(h, s); }
int EFModLoader::Loader::efclose(void *h) { return dlclose(h); }

void TEFModLoader::initialize(JNIEnv *env) {
    jclass stateClass = env->FindClass("eternal/future/State");
    if (stateClass == nullptr) {
        e_jni = false;
    } else {
        e_jni = true;
    }

    if (e_jni) {
        jfieldID modeFieldID = env->GetStaticFieldID(stateClass, "Mode", "I");
        jfieldID efmodcFieldID = env->GetStaticFieldID(stateClass, "EFMod_c", "Ljava/lang/String;");
        jint modeValue = env->GetStaticIntField(stateClass, modeFieldID);
        auto efmodcValue = (jstring) env->GetStaticObjectField(stateClass, efmodcFieldID);
        Mode = modeValue;
        modDataPath = env->GetStringUTFChars(efmodcValue, nullptr);
        auxiliaryPath = Utility::getModDir().parent_path() / "libauxiliary.so";
    }

    EFModLoader::Loader::loadMods(Utility::getModDir(), modDataPath);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    TEFModLoader::initialize(env);

    BNM::Loading::TryLoadByJNI(env);
    BNM::Loading::AddOnLoadedEvent(TEFModLoader::Manager::API::autoProcessing);
    BNM::Loading::AddOnLoadedEvent(EFModLoader::Loader::initiate);
    BNM::Loading::AddOnLoadedEvent(TEFModLoader::Hook::autoHook);

    return JNI_VERSION_1_6;
}