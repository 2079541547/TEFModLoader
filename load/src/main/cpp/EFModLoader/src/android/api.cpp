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
        // 尝试加载指定名称的Java类
        jclass loadedClass = env->FindClass(className);

        if (loadedClass == nullptr) {
            // 如果类未找到，则记录错误日志
            EFLOG(EFModLoader::LogLevel::ERROR, "EFModLoader", "Android::API", "callJavaMethod",
                  "无法加载类: " + std::string(className) + "，请检查类名是否正确或类是否已存在");
            return nullptr;
        }

        // 尝试获取指定名称和签名的Java方法ID
        jmethodID methodID = env->GetStaticMethodID(loadedClass, methodName, methodSignature);

        if (methodID == nullptr) {
            // 如果方法未找到，则记录错误日志
            EFLOG(EFModLoader::LogLevel::ERROR, "EFModLoader", "Android::API", "callJavaMethod",
                  "无法获取方法ID: " + std::string(methodName) + "，请检查方法名和签名是否正确");
            return nullptr;
        }

        // 使用提供的参数列表调用Java方法
        jobject result = env->CallStaticObjectMethodA(loadedClass, methodID, args.data());

        if (env->ExceptionCheck()) {
            // 如果Java方法调用过程中发生异常，则记录异常详情
            env->ExceptionDescribe();
            env->ExceptionClear();
            EFLOG(EFModLoader::LogLevel::ERROR, "EFModLoader", "Android::API", "callJavaMethod",
                  "调用方法: " + std::string(methodName) + "时发生异常，请查看上一条日志获取异常详情");
            return nullptr;
        }

        return result;
    }

    void callToast(JNIEnv *env, jobject context, const char *message) {
        // 准备Toast显示所需参数
        jvalue args[2];
        args[0].l = context; // 应用上下文
        args[1].l = env->NewStringUTF(message); // 显示的消息文本

        // 调用Toast显示方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/ToastApi", "show",
                                        "(Landroid/content/Context;Ljava/lang/String;)V", argVector);

        // 由于show方法没有返回值，因此这里应该总是得到nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callToast",
                  "意外的结果，显示Toast的方法不应该有返回值");
        }
    }

    void callILog(JNIEnv *env, const char *TAG, const char *message) {
        // 准备日志记录所需的参数
        jvalue args[2];
        args[0].l = env->NewStringUTF(TAG); // 日志标签
        args[1].l = env->NewStringUTF(message); // 日志消息

        // 调用日志记录方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/Log", "i",
                                        "(Ljava/lang/String;Ljava/lang/String;)V", argVector);

        // 由于日志记录方法没有返回值，因此这里应该总是得到nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callILog",
                  "意外的结果，记录日志的方法不应该有返回值");
        }
    }

    void callDLog(JNIEnv *env, const char *TAG, const char *message) {
        // 准备日志记录所需的参数
        jvalue args[2];
        args[0].l = env->NewStringUTF(TAG); // 日志标签
        args[1].l = env->NewStringUTF(message); // 日志消息

        // 调用日志记录方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/Log", "d",
                                        "(Ljava/lang/String;Ljava/lang/String;)V", argVector);

        // 由于日志记录方法没有返回值，因此这里应该总是得到nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callDLog",
                  "意外的结果，记录日志的方法不应该有返回值");
        }
    }

    void callELog(JNIEnv *env, const char *TAG, const char *message) {
        // 准备日志记录所需的参数
        jvalue args[2];
        args[0].l = env->NewStringUTF(TAG); // 日志标签
        args[1].l = env->NewStringUTF(message); // 日志消息

        // 调用日志记录方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/Log", "e",
                                        "(Ljava/lang/String;Ljava/lang/String;)V", argVector);

        // 由于日志记录方法没有返回值，因此这里应该总是得到nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callELog",
                  "意外的结果，记录日志的方法不应该有返回值");
        }
    }

    void callWLog(JNIEnv *env, const char *TAG, const char *message) {
        // 准备日志记录所需的参数
        jvalue args[2];
        args[0].l = env->NewStringUTF(TAG); // 日志标签
        args[1].l = env->NewStringUTF(message); // 日志消息

        // 调用日志记录方法
        std::vector<jvalue> argVector(args, args + 2);
        jobject result = callJavaMethod(env, "eternal/future/efmodloader/load/Log", "w",
                                        "(Ljava/lang/String;Ljava/lang/String;)V", argVector);

        // 由于日志记录方法没有返回值，因此这里应该总是得到nullptr
        if (result != nullptr) {
            EFLOG(EFModLoader::LogLevel::WARN, "EFModLoader", "Android::API", "callWLog",
                  "意外的结果，记录日志的方法不应该有返回值");
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