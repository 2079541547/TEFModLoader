/*******************************************************************************
 * 文件名称: api
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/9 上午4:10
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
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#include <EFModLoader/android/api.hpp>
#include <EFModLoader/android/Android.hpp>
#include <jni.h>
#include <EFModLoader/getData.hpp>
#include "EFModLoader/agreement.h"
#include <EFModLoader/api/RegisterApi.hpp>
#include <android/asset_manager_jni.h>
#include <fstream>
#include <EFModLoader/api/Redirect.hpp>

namespace EFModLoader::Android::API {

    // 加载类并调用Java方法
    jobject callJavaMethod(JNIEnv *env, const char *className, const char *methodName, const char *methodSignature, const std::vector<jvalue> &args) {
        // 加载类
        jclass loadedClass = env->FindClass(className);

        if (loadedClass == nullptr) {
            // 类未找到，处理错误
            EFLOG(EFModLoader::LogLevel::ERROR, "EFModLoader", "Android::API", "callJavaMethod", "Failed to load class: " + std::string(className));
            return nullptr;
        }

        // 获取方法ID
        jmethodID methodID = env->GetStaticMethodID(loadedClass, methodName, methodSignature);

        if (methodID == nullptr) {
            // 方法未找到，处理错误
            EFLOG(EFModLoader::LogLevel::ERROR, "EFModLoader", "Android::API", "callJavaMethod", "Failed to get method ID: " + std::string(methodName));
            return nullptr;
        }

        // 调用Java方法
        jobject result = env->CallStaticObjectMethodA(loadedClass, methodID, args.data());

        if (env->ExceptionCheck()) {
            // 处理异常
            env->ExceptionDescribe();
            env->ExceptionClear();
            return nullptr;
        }

        return result;
    }


    void callToast(JNIEnv *env, jobject context, const char *message) {
        // 准备参数
        jvalue args[2];
        args[0].l = context; // Context 对象
        args[1].l = env->NewStringUTF(message); // 消息字符串

        // 调用Java方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/ToastApi", "show", "(Landroid/content/Context;Ljava/lang/String;)V", argVector);

        // 由于showAgreement是void方法，所以result应该是nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callShowAgreement", "Unexpected result, should be nullptr");
        }
    }

    void callILog(JNIEnv *env, const char *TAG, const char *message) {
        // 准备参数
        jvalue args[2];
        args[0].l = env->NewStringUTF(TAG); // TAG
        args[1].l = env->NewStringUTF(message); // 消息字符串

        // 调用Java方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)V", argVector);

        // 由于showAgreement是void方法，所以result应该是nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callShowAgreement", "Unexpected result, should be nullptr");
        }
    }

    void callDLog(JNIEnv *env, const char *TAG, const char *message) {
        // 准备参数
        jvalue args[2];
        args[0].l = env->NewStringUTF(TAG); // TAG
        args[1].l = env->NewStringUTF(message); // 消息字符串

        // 调用Java方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)V", argVector);

        // 由于showAgreement是void方法，所以result应该是nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callShowAgreement", "Unexpected result, should be nullptr");
        }
    }

    void callELog(JNIEnv *env, const char *TAG, const char *message) {
        // 准备参数
        jvalue args[2];
        args[0].l = env->NewStringUTF(TAG); // TAG
        args[1].l = env->NewStringUTF(message); // 消息字符串

        // 调用Java方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)V", argVector);

        // 由于showAgreement是void方法，所以result应该是nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callShowAgreement", "Unexpected result, should be nullptr");
        }
    }

    void callWLog(JNIEnv *env, const char *TAG, const char *message) {
        // 准备参数
        jvalue args[2];
        args[0].l = env->NewStringUTF(TAG); // TAG
        args[1].l = env->NewStringUTF(message); // 消息字符串

        // 调用Java方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/Log", "w", "(Ljava/lang/String;Ljava/lang/String;)V", argVector);

        // 由于showAgreement是void方法，所以result应该是nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callShowAgreement", "Unexpected result, should be nullptr");
        }
    }

    void Register() {

        EFModLoader::RegisterApi::RegisterAPI("EFModLoader.android.callJavaMethod", EFModLoader::Redirect::getPtr(&callJavaMethod));
        EFModLoader::RegisterApi::RegisterAPI("EFModLoader.android.callToast", EFModLoader::Redirect::getPtr(&callToast));
        EFModLoader::RegisterApi::RegisterAPI("EFModLoader.android.log.i", EFModLoader::Redirect::getPtr(&callILog));
        EFModLoader::RegisterApi::RegisterAPI("EFModLoader.android.log.d", EFModLoader::Redirect::getPtr(&callDLog));
        EFModLoader::RegisterApi::RegisterAPI("EFModLoader.android.log.e", EFModLoader::Redirect::getPtr(&callELog));
        EFModLoader::RegisterApi::RegisterAPI("EFModLoader.android.log.w", EFModLoader::Redirect::getPtr(&callWLog));

    }

}