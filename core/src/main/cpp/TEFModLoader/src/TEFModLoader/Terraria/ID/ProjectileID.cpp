//
// Created by eternalfuture on 2024/10/26.
//

#include <TEFModLoader/Terraria/ID/ProjectileID.hpp>
#include <EFModLoader/api/RegisterApi.hpp>
#include <EFModLoader/api/Redirect.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <EFModLoader/hook/unity/RegisterHook.hpp>

namespace Terraria::ID::ProjectileID::Sets {

    Class Sets;
    MethodBase cctor;

    Field<Mono::Array<bool>>* DontApplyParryDamageBuff;
    Field<Mono::Array<bool>>* IsAGravestone;
    Field<Mono::Array<bool>>* IsAWhip;
    Field<Mono::Array<bool>>* AllowsContactDamageFromJellyfish;
    Field<Mono::Array<bool>>* ImmediatelyUpdatesNPCBuffFlags;
    Field<Mono::Array<bool>>* IsAnNPCAttachedExplosive;
    Field<Mono::Array<bool>>* WindPhysicsImmunity;
    Field<Mono::Array<bool>>* RocketsSkipDamageForPlayers;
    Field<Mono::Array<float>>* YoyosLifeTimeMultiplier;
    Field<Mono::Array<float>>* YoyosMaximumRange;
    Field<Mono::Array<bool>>* IsAGolfBall;
    Field<Mono::Array<float>>* YoyosTopSpeed;
    Field<Mono::Array<bool>>* CanDistortWater;
    Field<Mono::Array<bool>>* MinionShot;
    Field<Mono::Array<bool>>* SentryShot;
    Field<Mono::Array<bool>>* FallingBlockDoesNotFallThroughPlatforms;
    Field<Mono::Array<bool>>* ForcePlateDetection;
    Field<Mono::Array<int>>* TrailingMode;
    Field<Mono::Array<int>>* TrailCacheLength;
    Field<Mono::Array<bool>>* LightPet;
    Field<Mono::Array<bool>>* HeldProjDoesNotUsePlayerGfxOffY;
    Field<Mono::Array<bool>>* DontCancelChannelOnKill;
    Field<Mono::Array<bool>>* CultistIsResistantTo;
    Field<Mono::Array<bool>>* IsADD2Turret;
    Field<Mono::Array<bool>>* TurretFeature;
    Field<Mono::Array<bool>>* MinionTargettingFeature;
    Field<Mono::Array<bool>>* MinionSacrificable;
    Field<Mono::Array<bool>>* DontAttachHideToAlpha;
    Field<Mono::Array<float>>* ExtendedCanHitCheckRange;
    Field<Mono::Array<bool>>* NeedsUUID;
    Field<Mono::Array<bool>>* StardustDragon;
    Field<Mono::Array<bool>>* StormTiger;
    Field<Mono::Array<int>>* StormTigerIds;
    Field<Mono::Array<bool>>* IsARocketThatDealsDoubleDamageToPrimaryEnemy;
    Field<Mono::Array<bool>>* IsAMineThatDealsTripleDamageWhenStationary;
    Field<Mono::Array<bool>>* NoLiquidDistortion;
    Field<Mono::Array<bool>>* DismountsPlayersOnHit;
    Field<Mono::Array<bool>>* NoMeleeSpeedVelocityScaling;
    Field<Mono::Array<int>>* DrawScreenCheckFluff;
    Field<Mono::Array<bool>>* CanHitPastShimmer;

    void getHookPtr() {
        Sets = BNM::Class("Terraria.ID", "ProjectileID", BNM::Image("Assembly-CSharp.dll")).GetInnerClass("Sets");
        cctor = Sets.GetMethod(".cctor", 0);
    }

    void RegisterApi() {

        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.DontApplyParryDamageBuff", (uintptr_t)DontApplyParryDamageBuff);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.IsAGravestone", (uintptr_t)IsAGravestone);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.IsAWhip", (uintptr_t)IsAWhip);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.AllowsContactDamageFromJellyfish", (uintptr_t)AllowsContactDamageFromJellyfish);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.ImmediatelyUpdatesNPCBuffFlags", (uintptr_t)ImmediatelyUpdatesNPCBuffFlags);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.IsAnNPCAttachedExplosive", (uintptr_t)IsAnNPCAttachedExplosive);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.WindPhysicsImmunity", (uintptr_t)WindPhysicsImmunity);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.RocketsSkipDamageForPlayers", (uintptr_t)RocketsSkipDamageForPlayers);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.YoyosLifeTimeMultiplier", (uintptr_t)YoyosLifeTimeMultiplier);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.YoyosMaximumRange", (uintptr_t)YoyosMaximumRange);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.IsAGolfBall", (uintptr_t)IsAGolfBall);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.YoyosTopSpeed", (uintptr_t)YoyosTopSpeed);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.CanDistortWater", (uintptr_t)CanDistortWater);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.MinionShot", (uintptr_t)MinionShot);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.SentryShot", (uintptr_t)SentryShot);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.FallingBlockDoesNotFallThroughPlatforms", (uintptr_t)FallingBlockDoesNotFallThroughPlatforms);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.ForcePlateDetection", (uintptr_t)ForcePlateDetection);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.TrailingMode", (uintptr_t)TrailingMode);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.TrailCacheLength", (uintptr_t)TrailCacheLength);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.LightPet", (uintptr_t)LightPet);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.HeldProjDoesNotUsePlayerGfxOffY", (uintptr_t)HeldProjDoesNotUsePlayerGfxOffY);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.DontCancelChannelOnKill", (uintptr_t)DontCancelChannelOnKill);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.CultistIsResistantTo", (uintptr_t)CultistIsResistantTo);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.IsADD2Turret", (uintptr_t)IsADD2Turret);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.TurretFeature", (uintptr_t)TurretFeature);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.MinionTargettingFeature", (uintptr_t)MinionTargettingFeature);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.MinionSacrificable", (uintptr_t)MinionSacrificable);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.DontAttachHideToAlpha", (uintptr_t)DontAttachHideToAlpha);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.ExtendedCanHitCheckRange", (uintptr_t)ExtendedCanHitCheckRange);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.NeedsUUID", (uintptr_t)NeedsUUID);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.StardustDragon", (uintptr_t)StardustDragon);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.StormTiger", (uintptr_t)StormTiger);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.StormTigerIds", (uintptr_t)StormTigerIds);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.IsARocketThatDealsDoubleDamageToPrimaryEnemy", (uintptr_t)IsARocketThatDealsDoubleDamageToPrimaryEnemy);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.IsAMineThatDealsTripleDamageWhenStationary", (uintptr_t)IsAMineThatDealsTripleDamageWhenStationary);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.NoLiquidDistortion", (uintptr_t)NoLiquidDistortion);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.DismountsPlayersOnHit", (uintptr_t)DismountsPlayersOnHit);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.NoMeleeSpeedVelocityScaling", (uintptr_t)NoMeleeSpeedVelocityScaling);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.DrawScreenCheckFluff", (uintptr_t)DrawScreenCheckFluff);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ProjectileID.Sets.CanHitPastShimmer", (uintptr_t)CanHitPastShimmer);

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


    void* (*old_cctor)(UnityEngine::Object *);
    void* new_cctor(UnityEngine::Object *instance) {
        old_cctor(instance);

        DontApplyParryDamageBuff = new Field<Mono::Array<bool>>(Sets.GetField("DontApplyParryDamageBuff"));
        IsAGravestone = new Field<Mono::Array<bool>>(Sets.GetField("IsAGravestone"));
        IsAWhip = new Field<Mono::Array<bool>>(Sets.GetField("IsAWhip"));
        AllowsContactDamageFromJellyfish = new Field<Mono::Array<bool>>(Sets.GetField("AllowsContactDamageFromJellyfish"));
        ImmediatelyUpdatesNPCBuffFlags = new Field<Mono::Array<bool>>(Sets.GetField("ImmediatelyUpdatesNPCBuffFlags"));
        IsAnNPCAttachedExplosive = new Field<Mono::Array<bool>>(Sets.GetField("IsAnNPCAttachedExplosive"));
        WindPhysicsImmunity = new Field<Mono::Array<bool>>(Sets.GetField("WindPhysicsImmunity"));
        RocketsSkipDamageForPlayers = new Field<Mono::Array<bool>>(Sets.GetField("RocketsSkipDamageForPlayers"));
        YoyosLifeTimeMultiplier = new Field<Mono::Array<float>>(Sets.GetField("YoyosLifeTimeMultiplier"));
        YoyosMaximumRange = new Field<Mono::Array<float>>(Sets.GetField("YoyosMaximumRange"));
        IsAGolfBall = new Field<Mono::Array<bool>>(Sets.GetField("IsAGolfBall"));
        YoyosTopSpeed = new Field<Mono::Array<float>>(Sets.GetField("YoyosTopSpeed"));
        CanDistortWater = new Field<Mono::Array<bool>>(Sets.GetField("CanDistortWater"));
        MinionShot = new Field<Mono::Array<bool>>(Sets.GetField("MinionShot"));
        SentryShot = new Field<Mono::Array<bool>>(Sets.GetField("SentryShot"));
        FallingBlockDoesNotFallThroughPlatforms = new Field<Mono::Array<bool>>(Sets.GetField("FallingBlockDoesNotFallThroughPlatforms"));
        ForcePlateDetection = new Field<Mono::Array<bool>>(Sets.GetField("ForcePlateDetection"));
        TrailingMode = new Field<Mono::Array<int>>(Sets.GetField("TrailingMode"));
        TrailCacheLength = new Field<Mono::Array<int>>(Sets.GetField("TrailCacheLength"));
        LightPet = new Field<Mono::Array<bool>>(Sets.GetField("LightPet"));
        HeldProjDoesNotUsePlayerGfxOffY = new Field<Mono::Array<bool>>(Sets.GetField("HeldProjDoesNotUsePlayerGfxOffY"));
        DontCancelChannelOnKill = new Field<Mono::Array<bool>>(Sets.GetField("DontCancelChannelOnKill"));
        CultistIsResistantTo = new Field<Mono::Array<bool>>(Sets.GetField("CultistIsResistantTo"));
        IsADD2Turret = new Field<Mono::Array<bool>>(Sets.GetField("IsADD2Turret"));
        TurretFeature = new Field<Mono::Array<bool>>(Sets.GetField("TurretFeature"));
        MinionTargettingFeature = new Field<Mono::Array<bool>>(Sets.GetField("MinionTargettingFeature"));
        MinionSacrificable = new Field<Mono::Array<bool>>(Sets.GetField("MinionSacrificable"));
        DontAttachHideToAlpha = new Field<Mono::Array<bool>>(Sets.GetField("DontAttachHideToAlpha"));
        ExtendedCanHitCheckRange = new Field<Mono::Array<float>>(Sets.GetField("ExtendedCanHitCheckRange"));
        NeedsUUID = new Field<Mono::Array<bool>>(Sets.GetField("NeedsUUID"));
        StardustDragon = new Field<Mono::Array<bool>>(Sets.GetField("StardustDragon"));
        StormTiger = new Field<Mono::Array<bool>>(Sets.GetField("StormTiger"));
        StormTigerIds = new Field<Mono::Array<int>>(Sets.GetField("StormTigerIds"));
        IsARocketThatDealsDoubleDamageToPrimaryEnemy = new Field<Mono::Array<bool>>(Sets.GetField("IsARocketThatDealsDoubleDamageToPrimaryEnemy"));
        IsAMineThatDealsTripleDamageWhenStationary = new Field<Mono::Array<bool>>(Sets.GetField("IsAMineThatDealsTripleDamageWhenStationary"));
        NoLiquidDistortion = new Field<Mono::Array<bool>>(Sets.GetField("NoLiquidDistortion"));
        DismountsPlayersOnHit = new Field<Mono::Array<bool>>(Sets.GetField("DismountsPlayersOnHit"));
        NoMeleeSpeedVelocityScaling = new Field<Mono::Array<bool>>(Sets.GetField("NoMeleeSpeedVelocityScaling"));
        DrawScreenCheckFluff = new Field<Mono::Array<int>>(Sets.GetField("DrawScreenCheckFluff"));
        CanHitPastShimmer = new Field<Mono::Array<bool>>(Sets.GetField("CanHitPastShimmer"));

        auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("Assembly-CSharp.dll.Terraria.ID.ProjectileID.Sets..cctor");
        for (auto hook : hooks) {
            EFModLoader::Redirect::callFunction<void>(reinterpret_cast<void *>(hook));
        }
        return nullptr;
    }


    void RegisterHook() {
        using namespace EFModLoader::RegisterHook::Unity;
        RegisterIHOOK("Assembly-CSharp.dll.Terraria.ID.ProjectileID.Sets..cctor", cctor, new_cctor, old_cctor);
    }

}