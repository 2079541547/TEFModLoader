/*******************************************************************************
 * 文件名称: multi_channel
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

#include <string>

/**
 * @class MultiChannel
 * @brief 多通道数据交换器（线程安全通信管道）
 *
 * 提供基于ID标识的多类型数据交换能力，支持跨模块通信。
 * 所有方法保证线程安全，数据所有权由调用方管理。
 *
 * @see EFMod 使用本类作为模块间通信载体
 */
class MultiChannel {
    /**
     * @brief 从通道获取原始数据
     * @param[in] id 通道标识符
     * @return 数据指针（可能为nullptr）
     * @warning 返回指针的生命周期不确定，建议立即处理不存储
     */
    virtual void* get(const std::string& id) = 0;

public:
    virtual ~MultiChannel() = default;

    /**
     * @brief 发送数据到指定通道
     * @param[in] id 通道标识符
     * @param[in] data 待发送数据指针（生命周期由调用方维护）
     * @throws std::out_of_range 当id未注册时抛出
     * @note 通道ID应在系统启动时统一分配
     */
    virtual void send(const std::string& id, void* data) = 0;

    /**
     * @brief 接收并转换类型数据（模板方法）
     * @tparam T 目标数据类型（需可安全指针转换）
     * @param[in] id 通道标识符
     * @return 类型转换后的指针，失败返回nullptr
     */
    template<typename T>
    T receive(const std::string& id) { return reinterpret_cast<T>(get(id)); }
};