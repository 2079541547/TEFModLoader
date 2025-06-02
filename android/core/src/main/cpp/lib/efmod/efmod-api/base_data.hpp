/*******************************************************************************
 * 文件名称: base_data
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
 * @struct LoadConfig
 * @brief 模块加载配置参数
 *
 * 用于控制模块初始化行为及日志标识配置
 */
struct LoadConfig {
    bool Initialize = false; /**< 是否初始化（在主线程执行Mod初始化函数） */
};

/**
 * @enum ModuleType
 * @brief 模块类型分类
 *
 * 用于区分不同功能的模块，影响模块管理策略
 */
enum class ModuleType {
    Game,        /**< 游戏逻辑模块（如关卡、玩法系统） */
    Content,     /**< 内容扩展模块（如DLC、模组） */
    Library,     /**< 公共库模块（如数学库、网络库） */
    Interface,   /**< 用户界面模块（如HUD、菜单系统） */
    System       /**< 核心系统模块（如内存管理、资源加载） */
};

/**
 * @struct Metadata
 * @brief 模块元数据描述
 *
 * 包含模块基础信息和加载配置，由 EFMod::GetMetadata() 返回
 *
 * @see EFMod::GetMetadata()
 */
struct Metadata {
    std::string name;    /**< 模块名称（英文标识符，如"PhysicsModule"） */
    std::string author;  /**< 作者/团队信息（如"NetEase-Group"） */
    std::string version; /**< 语义化版本号（如"1.2.0"） */
    int standard;        /**< 兼容性标准版本号（数字编码） */
    ModuleType type;     /**< 模块类型，影响加载优先级 */
    LoadConfig config;   /**< 模块专属加载配置 */
};