//
// Created by eternalfuture on 2024/10/26.
//

#include <TEFModLoader/Terraria/ID/BuffID.hpp>
#include <EFModLoader/api/RegisterApi.hpp>
#include <EFModLoader/api/Redirect.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <EFModLoader/hook/unity/RegisterHook.hpp>

namespace Terraria::ID::BuffID::Sets {

    Class Sets;
    MethodBase cctor;

    Field<Mono::Array<bool>>* IsWellFed;
    Field<Mono::Array<bool>>* IsFedState;
    Field<Mono::Array<bool>>* IsAnNPCWhipDebuff;
    Field<Mono::Array<bool>>* TimeLeftDoesNotDecrease;
    Field<Mono::Array<bool>>* CanBeRemovedByNetMessage;
    Field<Mono::Array<bool>>* IsAFlaskBuff;
    Field<Mono::Array<bool>>* NurseCannotRemoveDebuff;


    void getHookPtr(){
        Sets = BNM::Class("Terraria.ID", "BuffID", BNM::Image("Assembly-CSharp.dll")).GetInnerClass("Sets");
        cctor = Sets.GetMethod(".cctor", 0);
    }

    void RegisterApi() {
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.BuffID.Sets.IsWellFed", (uintptr_t)IsWellFed);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.BuffID.Sets.IsFedState", (uintptr_t)IsFedState);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.BuffID.Sets.IsAnNPCWhipDebuff", (uintptr_t)IsAnNPCWhipDebuff);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.BuffID.Sets.TimeLeftDoesNotDecrease", (uintptr_t)TimeLeftDoesNotDecrease);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.BuffID.Sets.CanBeRemovedByNetMessage", (uintptr_t)CanBeRemovedByNetMessage);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.BuffID.Sets.IsAFlaskBuff", (uintptr_t)IsAFlaskBuff);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.BuffID.Sets.NurseCannotRemoveDebuff", (uintptr_t)NurseCannotRemoveDebuff);


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

        IsWellFed = new Field<Mono::Array<bool>>(Sets.GetField("IsWellFed"));
        IsFedState = new Field<Mono::Array<bool>>(Sets.GetField("IsFedState"));
        IsAnNPCWhipDebuff = new Field<Mono::Array<bool>>(Sets.GetField("IsAnNPCWhipDebuff"));
        TimeLeftDoesNotDecrease = new Field<Mono::Array<bool>>(Sets.GetField("TimeLeftDoesNotDecrease"));
        CanBeRemovedByNetMessage = new Field<Mono::Array<bool>>(Sets.GetField("CanBeRemovedByNetMessage"));
        IsAFlaskBuff = new Field<Mono::Array<bool>>(Sets.GetField("IsAFlaskBuff"));
        NurseCannotRemoveDebuff = new Field<Mono::Array<bool>>(Sets.GetField("NurseCannotRemoveDebuff"));

        RegisterApi();


        auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("Assembly-CSharp.dll.Terraria.ID.BuffID.Sets..cctor");
        for (auto hook : hooks) {
            EFModLoader::Redirect::callFunction<void>(reinterpret_cast<void *>(hook));
        }
    }



    void RegisterHook() {
        using namespace EFModLoader::RegisterHook::Unity;
        RegisterIHOOK("Assembly-CSharp.dll.Terraria.ID.BuffID.Sets..cctor", cctor, new_cctor, old_cctor);
    }
}