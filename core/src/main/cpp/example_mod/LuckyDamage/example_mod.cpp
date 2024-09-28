//
// Created by eternalfuture on 2024/9/17.
//
#include "../../EFModLoader/include/EFMod/EFMod.hpp"
#include <iostream>
#include <vector>
#include <android/log.h>
#include "igc.hpp"


class LuckyDamage : public EFMod {
public:

    const char * GetIdentifier() const override {
        return "LuckyDamage";
    }

    bool Initialize() override {return true;}

    void RegisterHooks() override {
        __android_log_print(ANDROID_LOG_INFO, "LuckyDamage", "Registering hooks for mod: %s", GetIdentifier());

        // 注册多个扩展函数
        std::vector<std::pair<std::string, uintptr_t>> hooks{
                {"Assembly-CSharp.dll.Terraria.Main.DamageVar", reinterpret_cast<uintptr_t>(&Limit_Damage)},
        };

        // 遍历并注册hook
        for (const auto& [hookPoint, funcPtr] : hooks) {
            modAPI->RegisterExtension(hookPoint, funcPtr);
        }
    }

    void RegisterAPIs() override {}

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
