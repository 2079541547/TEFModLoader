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


    void RegisterHook() {
        using namespace EFModLoader::RegisterHook::Unity;

        RegisterHOOK("Assembly-CSharp.dll.Terraria.Main.DamageVar",
                     DamageVar,
                     (void *) new_DamageVar,
                     (void **) old_DamageVar);
    }


    int (*old_DamageVar)(UnityEngine::Object *, float dmg, float luck);
    int new_DamageVar(BNM::UnityEngine::Object *instance, float dmg, float luck) {
        auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("Assembly-CSharp.dll.Terraria.Main.DamageVar");
        for (auto hook : hooks) {
            return EFModLoader::Redirect::callFunction<int>(reinterpret_cast<void *>(hook));
        }
        return 0;
    }
}