//
// Created by eternalfuture on 2024/10/19.
//

#include "../../EFModLoader/include/EFModLoader/EFMod/EFMod.hpp"
#include <BNM/Field.hpp>
#include <BNM/Class.hpp>
#include <BNM/MethodBase.hpp>
#include <BNM/UnityStructures.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <android/log.h>
#include <BNM/UnityStructures.hpp>


BNM::MethodBase* a;

BNM::Structures::Mono::String (*old_get_bytes)(BNM::UnityEngine::Object *);
BNM::Structures::Mono::String new_get_bytes(BNM::UnityEngine::Object *instance) {
    BNM_LOG_DEBUG("已劫持Unity获取字节函数");
    return old_get_bytes(instance);
}


class ForgottenTreasure : public EFMod {
public:

    const char * GetIdentifier() const override {
        return "ForgottenTreasure";
    }

    bool Initialize() override {

        return true;
    }

    void RegisterHooks() override {
        modAPI->RegisterExtension("UnityEngine.CoreModule.dll.UnityEngine.get_text", reinterpret_cast<uintptr_t>(new_get_bytes));
    }

    void RegisterAPIs() override {
        modAPI->RegisterAPI("UnityEngine.CoreModule.dll.UnityEngine.ToString", reinterpret_cast<uintptr_t>(&a));
        modAPI->RegisterAPI("UnityEngine.CoreModule.dll.UnityEngine.get_text.old", reinterpret_cast<uintptr_t>(&old_get_bytes));
    }

    void LoadEFMod(EFModLoaderAPI* Mod) override { modAPI = Mod; }


private:
    EFModLoaderAPI* modAPI = nullptr;
};

extern "C" EFMod* GetModInstance() {
    static ForgottenTreasure modInstance;
    return &modInstance;
}
