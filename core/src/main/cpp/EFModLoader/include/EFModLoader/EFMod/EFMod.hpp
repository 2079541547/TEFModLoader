/*******************************************************************************
 * 文件名称: EFMod
 * 项目名称: EFModLoader
 * 创建时间: 2024/9/28
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This project is licensed under the MIT License.
 *
 *         Permission is hereby granted, free of charge, to any person obtaining a copy
 *         of this software and associated documentation files (the "Software"), to deal
 *         in the Software without restriction, including without limitation the rights
 *         to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *         copies of the Software, and to permit persons to whom the Software is
 *         furnished to do so, subject to the following conditions:
 *
 *         The above copyright notice and this permission notice shall be included in all
 *         copies or substantial portions of the Software.
 *
 *         THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *         IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *         FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *         AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *         LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *         OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *         SOFTWARE.
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守MIT协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/


#pragma once

#include <iostream>
#include <memory>
#include <type_traits>
#include <utility>
#include <string>
#include <functional>
#include "EFModLoaderAPI.hpp"


class EFMod {
public:
    virtual ~EFMod() {}

    // 获取模组的唯一标识符
    virtual const char* GetIdentifier() const = 0;

    // 模组初始化时调用
    virtual bool Initialize() = 0;

    // 注册模组要hook的位置和hook后的函数指针
    virtual void RegisterHooks() = 0;

    //注册API
    virtual void RegisterAPIs() = 0;

    // 接收加载器提供的API集合
    virtual void LoadEFMod(EFModLoaderAPI* api) = 0;
};