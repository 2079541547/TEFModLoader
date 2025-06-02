/*******************************************************************************
 * 文件名称: efmod_core
 * 项目名称: EFMod
 * 创建时间: 25-5-4
 * 作者: EternalFuture゙
 * Gitlab: https://gitlab.com/2079541547/
 * 协议: MIT
 *
 * MIT License
 *
 * Copyright (c) 2025 EternalFuture゙
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
 
 
#pragma once

#include "base_data.hpp"
#include "multi_channel.hpp"

/**
 * @brief EFMod（扩展功能模块）插件的抽象基类
 *
 * 定义动态加载/卸载模块的通用接口，支持通过 MultiChannel 通信，
 * 并支持元数据查询。所有具体实现必须重写纯虚方法。
 */
class EFMod {
public:
    virtual ~EFMod() = default;

    /**
     * @brief 初始化模块
     * @return 成功返回 0，失败返回非零错误码
     * @note Load后立即调用且在主线程中，Initialize需要设置为true，否则不会调用
     * @warning 禁止在方法内执行耗时操作
     * @param[in] path 当前Mod私有目录路径
     * @param[in] multiChannel 接收并使用API
     */
    virtual int Initialize(const std::string& path, MultiChannel* multiChannel) = 0;

    /**
     * @brief 通过多通道发送数据
     * @param[in] path 当前Mod私有目录路径
     * @param[in] multiChannel 多通道对象指针
     */
    virtual void Send(const std::string& path, MultiChannel* multiChannel) = 0;

    /**
     * @brief 通过多通道接收数据
     * @param[in] path 当前Mod私有目录路径
     * @param[in] multiChannel 多通道对象指针
     */
    virtual void Receive(const std::string& path, MultiChannel* multiChannel) = 0;

    /**
     * @brief 从指定路径加载模块资源
     * @param[in] path 当前Mod私有目录路径
     * @param[in] multiChannel 接受默认API
     * @return 成功返回 0，失败返回错误码
     * @retval -1 路径无效
     * @retval -2 资源格式错误
     */
    virtual int Load(const std::string& path, MultiChannel* multiChannel) = 0;

    /**
     * @brief 卸载模块资源
     * @return 成功返回 0，资源未加载返回 1
     * @post 调用后模块应恢复到可重新加载状态
     * @param[in] path 当前Mod私有目录路径
     * @param[in] multiChannel 接收并使用API
     */
    virtual int UnLoad(const std::string &path, MultiChannel *multiChannel) = 0;

    /**
     * @brief 获取模块元数据
     * @return Metadata 结构体，包含：
     *         - 模块名称
     *         - 版本号
     *         - 作者信息
     *         ...
     */
    virtual Metadata GetMetadata() = 0;
};

/**
 * @brief 模块工厂函数（C接口）
 * @return 新创建的模块对象指针
 * @note 如下: EFMod * CreateMod() {
    static MyMod Mod;
    return Mod;
    }
 */
extern "C" EFMod* CreateMod();