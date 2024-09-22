//
// Created by eternalfuture on 2024/9/17.
//
#include "../EFModLoader/include/EFMod/EFMod.hpp"
#include <iostream>
#include <vector>
#include <android/log.h>

std::string* GetStorage;

// 扩展函数1
int MyHookedFunction1() {

    __android_log_print(ANDROID_LOG_INFO, "ExampleMod", "Hooked function 1 called for mod");
    // hook后的逻辑
    return 114514;
}


class ExampleMod : public EFMod {
public:

    const char * GetIdentifier() const override {
        return "example_mod";
    }


    bool Initialize() override {
        //GetStorage
        __android_log_print(ANDROID_LOG_INFO, "ExampleMod", "Initializing mod: %s", GetIdentifier());

        __android_log_print(ANDROID_LOG_INFO, "ExampleMod", "GetStorage: %s", GetStorage->c_str());
        return true;
    }

    void Shutdown() override {

        __android_log_print(ANDROID_LOG_INFO, "ExampleMod", "Shutting down mod: %s", GetIdentifier());
    }

    void RegisterHooks() override {
        __android_log_print(ANDROID_LOG_INFO, "ExampleMod", "Registering hooks for mod: %s", GetIdentifier());

        // 注册多个扩展函数
        std::vector<std::pair<std::string, uintptr_t>> hooks{
                {"hook_point_1", reinterpret_cast<uintptr_t>(&MyHookedFunction1)},
        };

        // 遍历并注册hook
        for (const auto& [hookPoint, funcPtr] : hooks) {
            modAPI->RegisterExtension(hookPoint, funcPtr);
        }
    }

    void RegisterAPIs() override {
        __android_log_print(ANDROID_LOG_INFO, "ExampleMod", "Registering APIs for mod: %s", GetIdentifier());

        // 注册多个API
        std::vector<std::pair<std::string, uintptr_t>> hooks{
                {"GetStorage", reinterpret_cast<uintptr_t>(&GetStorage)},
        };

        // 遍历并注册hook
        for (const auto& [hookPoint, funcPtr] : hooks) {
            modAPI->RegisterAPI(hookPoint, funcPtr);
        }
    }

    void LoadEFMod(EFModLoaderAPI* Mod) override {
        // 保存Mod信息
        modAPI = Mod;
    }


private:
    EFModLoaderAPI* modAPI = nullptr;
};

extern "C" EFMod* GetModInstance() {
    static ExampleMod modInstance;
    return &modInstance;
}
