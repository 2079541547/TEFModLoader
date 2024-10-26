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

void (*old_get_bytes)(BNM::UnityEngine::Object *);
void new_get_bytes(BNM::UnityEngine::Object *instance) {
    BNM_LOG_DEBUG("测试Hook");
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
        modAPI->RegisterExtension("Assembly-CSharp.dll.Terraria.ID.PrefixID.Sets..cctor", reinterpret_cast<uintptr_t>(new_get_bytes));
    }

    void RegisterAPIs() override {}

    void LoadEFMod(EFModLoaderAPI* Mod) override { modAPI = Mod; }


private:
    EFModLoaderAPI* modAPI = nullptr;
};

extern "C" EFMod* GetModInstance() {
    static ForgottenTreasure modInstance;
    return &modInstance;
}
