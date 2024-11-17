package eternal.future.efmodloader.load;

import android.content.Context;
import android.widget.Toast;

/*******************************************************************************
<<<<<<<< HEAD:load/src/main/cpp/EFModLoader/include/EFModLoader/android/api.hpp
 * 文件名称: api
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/9 上午4:07
========
 * 文件名称: Toast
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/9 上午4:19
>>>>>>>> main:load/src/main/java/eternal/future/efmodloader/load/ToastApi.java
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


<<<<<<<< HEAD:load/src/main/cpp/EFModLoader/include/EFModLoader/android/api.hpp
#include <iostream>
#include <jni.h>
<<<<<<<< HEAD:load/src/main/cpp/EFModLoader/include/EFModLoader/android/api.hpp
#include "../api/RegisterApi.hpp"
========
#include <EFModLoader/android/api.hpp>
>>>>>>>> main:load/src/main/cpp/EFModLoader/include/EFModLoader/android/Android.hpp

namespace EFModLoader::Android::API {

    using namespace std;
<<<<<<<< HEAD:load/src/main/cpp/EFModLoader/include/EFModLoader/android/api.hpp
    
    void Register();

========

    extern string* get_PackageName;
    extern string* get_cacheDir;

    void Load(JNIEnv *env, const string& EFModLoader);
    void Load_Loader(JNIEnv *env, const string& EFModLoader);
>>>>>>>> main:load/src/main/cpp/EFModLoader/include/EFModLoader/android/Android.hpp
========
/**
 * 自定义Toast工具类，用于在Android应用中显示高度自定义的Toast消息。
 * 提供了多种方法来设置Toast的持续时间、位置、背景颜色、文本颜色等。
 */
public class ToastApi {

    /**
     * 显示一个默认的Toast消息。
     *
     * @param context 应用上下文。
     * @param message 要显示的消息。
     */
    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
>>>>>>>> main:load/src/main/java/eternal/future/efmodloader/load/ToastApi.java
}