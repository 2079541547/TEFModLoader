//
// Created by eternalfuture on 2024/9/28.
//

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