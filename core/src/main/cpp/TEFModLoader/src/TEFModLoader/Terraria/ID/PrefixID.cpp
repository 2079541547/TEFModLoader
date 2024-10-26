//
// Created by eternalfuture on 2024/10/26.
//

#include <TEFModLoader/Terraria/ID/PrefixID.hpp>
#include <EFModLoader/api/RegisterApi.hpp>
#include <EFModLoader/api/Redirect.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <EFModLoader/hook/unity/RegisterHook.hpp>

namespace Terraria::ID::PrefixID::Sets {

    Class Sets;
    MethodBase cctor;

    Field<Mono::Array<bool>>* ReducedNaturalChance;


    void getHookPtr(){
        Sets = BNM::Class("Terraria.ID", "PrefixID", BNM::Image("Assembly-CSharp.dll")).GetInnerClass("Sets");
        cctor = Sets.GetMethod(".cctor", 0);
    }

    void RegisterApi() {
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.PrefixID.Sets.ReducedNaturalChance", (uintptr_t)ReducedNaturalChance);


        for (const auto& api : EFModLoader::RegisterApi::registerAPI) {
            if (EFModLoaderAPI::GetEFModLoader().FindAPIS(api.apiName).empty()) {
                EFModLoader::Log::LOG("Warning", "RegisterApi", "Register", "没有Mod注册的api：" + api.apiName);
                void* ptr = reinterpret_cast<void*>(api.new_ptr);
                try {
                    delete[] reinterpret_cast<Field<bool>*>(ptr);
                    EFModLoader::Log::LOG("Info", "RegisterApi", "Register", "尝试卸载未使用的API成功：" + api.apiName);
                } catch (...) {
                    EFModLoader::Log::LOG("Warning", "RegisterApi", "Register", "尝试卸载未使用的API失败：" + api.apiName);
                }
            } else {
                for (auto a: EFModLoaderAPI::GetEFModLoader().FindAPIS(api.apiName)) {
                    EFModLoader::Redirect::redirectPointer<void*>(a, api.new_ptr);
                }
                EFModLoader::Log::LOG("Info", "RegisterApi", "Register", "已注册api：" + api.apiName);
            }
        }
        // 清空注册列表，防止重复注册
        EFModLoader::RegisterApi::registerAPI.clear();
    }


    void (*old_cctor)(UnityEngine::Object *);
    void new_cctor(UnityEngine::Object *instance) {
        old_cctor(instance);

        ReducedNaturalChance = new Field<Mono::Array<bool>>(Sets.GetField("ReducedNaturalChance"));

        RegisterApi();

        auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("Assembly-CSharp.dll.Terraria.ID.PrefixID.Sets..cctor");
        for (auto hook : hooks) {
            EFModLoader::Redirect::callFunction<void>(reinterpret_cast<void *>(hook));
        }
    }



    void RegisterHook() {
        using namespace EFModLoader::RegisterHook::Unity;
        RegisterIHOOK("Assembly-CSharp.dll.Terraria.ID.PrefixID.Sets..cctor", cctor, new_cctor, old_cctor);
    }
}