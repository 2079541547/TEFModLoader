//
// Created by eternalfuture on 2024/10/20.
//

#include <TEFModLoader/Register.hpp>
#include <EFModLoader/hook/unity/RegisterHook.hpp>
#include <EFModLoader/api/RegisterApi.hpp>
#include <TEFModLoader/Terraria/Main.hpp>
#include <TEFModLoader/Terraria/ID/ItemID.hpp>
#include <EFModLoader/log.hpp>
#include <TEFModLoader/UnityEngine/TextAsset.hpp>
#include <TEFModLoader/Terraria/ID/BuffID.hpp>
#include <TEFModLoader/Terraria/ID/ProjectileID.hpp>
#include <TEFModLoader/Terraria/ID/PrefixID.hpp>


namespace TEFModLoader::Register {

    using namespace std;

    namespace API {
        string* get_PackageName;
        string* get_ExternalDir;
        string* get_cacheDir;
    }

    void RegisterAPI() {
        EFLOG(EFModLoader::LogLevel::DEBUG, "TEFModLoader", "Register", "RegisterAPI", "正在注册API...");

        EFModLoader::RegisterApi::RegisterAPI("get_PackageName", (long) API::get_PackageName);
        EFModLoader::RegisterApi::RegisterAPI("get_ExternalDir", (long) API::get_ExternalDir);
        EFModLoader::RegisterApi::RegisterAPI("get_cacheDir", (long) API::get_cacheDir);
    }

    void RegisterPtr() {
        EFLOG(EFModLoader::LogLevel::DEBUG, "TEFModLoader", "Register", "RegisterPtr", "正在查找需要被Hook的内存地址...");

        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::Main::getHookPtr);
        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::ID::ItemID::Sets::getHookPtr);
        EFModLoader::RegisterHook::Unity::RegisterLoad(UnityEngine::TextAsset::getHookPtr);
        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::ID::BuffID::Sets::getHookPtr);
        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::ID::ProjectileID::Sets::getHookPtr);
        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::ID::PrefixID::Sets::getHookPtr);

    }

    void RegisterHook() {
        EFLOG(EFModLoader::LogLevel::DEBUG, "TEFModLoader", "Register", "RegisterHook", "正在注册Hook节点...");

        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::Main::RegisterHook);
        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::ID::ItemID::Sets::RegisterHook);
        EFModLoader::RegisterHook::Unity::RegisterLoad(UnityEngine::TextAsset::RegisterHook);
        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::ID::BuffID::Sets::RegisterHook);
        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::ID::ProjectileID::Sets::RegisterHook);
        EFModLoader::RegisterHook::Unity::RegisterLoad(Terraria::ID::PrefixID::Sets::RegisterHook);
    }


}
