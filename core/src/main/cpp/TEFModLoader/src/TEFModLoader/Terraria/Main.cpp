//
// Created by eternalfuture on 2024/10/20.
//

#include <TEFModLoader/Terraria/Main.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <EFModLoader/api/Redirect.hpp>
#include <EFModLoader/hook/unity/RegisterHook.hpp>


namespace Terraria::Main {

    Class Main;
    MethodBase DamageVar;

    void getHookPtr() {
        Main = BNM::Class("Terraria", "Main", BNM::Image("Assembly-CSharp.dll"));
        DamageVar = Terraria::Main::Main.GetMethod("DamageVar", 2);
    }

    int (*old_DamageVar)(float* dmg, float* luck);
    int new_DamageVar(float* dmg, float* luck) {

        auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("Assembly-CSharp.dll.Terraria.Main.DamageVar");
        for (auto hook : hooks) {
            auto a = EFModLoader::Redirect::callFunction<int>(reinterpret_cast<void *>(hook));
            if (old_DamageVar(dmg, luck) != a) return a;
        }
        return old_DamageVar(dmg, luck);
    }

    void RegisterHook() {
        using namespace EFModLoader::RegisterHook::Unity;

        RegisterHOOK("Assembly-CSharp.dll.Terraria.Main.DamageVar",
                     DamageVar,
                     (void *) new_DamageVar,
                     (void **) old_DamageVar);
    }
}