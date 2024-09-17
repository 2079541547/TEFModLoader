//
// Created by eternalfuture on 2024/9/17.
//

#ifndef EFMODLOADER_IMOD_H
#define EFMODLOADER_IMOD_H


#include <iostream>
#include <memory>
#include <type_traits>
#include <utility>
#include <string>
#include <functional>
#include "IModLoaderAPI.h"


class IMod {
public:
    virtual ~IMod() {}

    // 获取模组的唯一标识符
    virtual const char* GetIdentifier() const = 0;

    // 模组初始化时调用
    virtual bool Initialize() = 0;

    // 模组卸载时调用
    virtual void Shutdown() = 0;

    // 注册模组要hook的位置和hook后的函数指针
    virtual void RegisterHooks() = 0;

    // 接收加载器提供的API集合
    virtual void ProvideAPI(ModLoaderAPI* api) = 0;
};


#endif //EFMODLOADER_IMOD_H
