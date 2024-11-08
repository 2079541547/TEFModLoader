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

#include <string>
#include "EFModLoaderAPI.hpp"

/**
 * @class EFMod
 * @brief 抽象基类，定义了模组的基本接口。
 *
 * 每个模组必须继承自这个类，并实现相应的虚函数。
 */
class EFMod {
public:
    virtual ~EFMod() {}

    /**
     * @fn GetIdentifier
     * @brief 获取模组的唯一标识符。
     *
     * 每个模组必须提供一个唯一的标识符，用于区分不同的模组。
     *
     * @return 返回模组的唯一标识符。
     */
    virtual const char* GetIdentifier() const = 0;

    /**
     * @fn Initialize
     * @brief 模组初始化时调用。
     *
     * 在模组加载时，此函数会被调用，用于执行必要的初始化操作。
     *
     * @return 返回true表示初始化成功，false表示初始化失败。
     */
    virtual bool Initialize() = 0;

    /**
     * @fn RegisterHooks
     * @brief 注册模组要hook的位置和hook后的函数指针。
     *
     * 模组可以通过此函数注册需要hook的函数及其新的实现。
     */
    virtual void RegisterHooks() = 0;

    /**
     * @fn RegisterAPIs
     * @brief 注册模组提供的API。
     *
     * 模组可以通过此函数注册它提供的API，供其他模组或加载器使用。
     */
    virtual void RegisterAPIs() = 0;

    /**
     * @fn LoadEFMod
     * @brief 接收加载器提供的API集合。
     *
     * 模组通过此函数接收加载器提供的API集合，以便在初始化时使用这些API。
     *
     * @param api 加载器提供的API集合。
     */
    virtual void LoadEFMod(EFModLoaderAPI* api) = 0;
};