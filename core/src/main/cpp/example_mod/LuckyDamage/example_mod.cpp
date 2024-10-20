//
// Created by eternalfuture on 2024/9/17.
//
#include "../../EFModLoader/include/EFModLoader/EFMod/EFMod.hpp" //这是必须导入的，有依赖的函数调用
#include <iostream>
#include <vector>
#include <android/log.h>
#include "igc.hpp"


//std::string *a;


class LuckyDamage : public EFMod {
public:

    const char * GetIdentifier() const override {
        return "LuckyDamage";
    }

    bool Initialize() override {

        //__android_log_print(ANDROID_LOG_INFO, "LuckyDamage", "尝试获取包名: %s", a->c_str());

        return true;
    }

    void RegisterHooks() override {
        __android_log_print(ANDROID_LOG_INFO, "LuckyDamage", "Registering hooks for mod: %s", GetIdentifier());

        // 注册多个扩展函数
        std::vector<std::pair<std::string, uintptr_t>> hooks{
                {"Assembly-CSharp.dll.Terraria.Main.DamageVar", reinterpret_cast<uintptr_t>(Limit_Damage)},
        };

        // 遍历并注册hook
        for (const auto& [hookPoint, funcPtr] : hooks) {
            modAPI->RegisterExtension(hookPoint, funcPtr);
        }
    }

    void RegisterAPIs() override {

        //modAPI->RegisterAPI("PackName", reinterpret_cast<uintptr_t>(&a));

    }

    void LoadEFMod(EFModLoaderAPI* Mod) override {
        // 保存Mod信息
        modAPI = Mod;
    }


private:
    EFModLoaderAPI* modAPI = nullptr;
};

extern "C" EFMod* GetModInstance() {
    static LuckyDamage modInstance;
    return &modInstance;
}
