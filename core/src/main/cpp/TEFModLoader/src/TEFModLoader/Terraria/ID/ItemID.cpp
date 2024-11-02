//
// Created by eternalfuture on 2024/10/20.
//

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <TEFModLoader/Terraria/ID/ItemID.hpp>
#include <EFModLoader/api/RegisterApi.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <EFModLoader/api/Redirect.hpp>
#include <EFModLoader/hook/unity/RegisterHook.hpp>
#include <BNM/BasicMonoStructures.hpp>


namespace Terraria::ID::ItemID::Sets {

    Class Sets;
    MethodBase cctor;

    Field<Mono::List<int>>* ItemsThatAreProcessedAfterNormalContentSample;
    Field<Mono::Array<bool>>* IsLavaImmuneRegardlessOfRarity;
    Field<Mono::Array<bool>>* ItemsThatAllowRepeatedRightClick;
    Field<Mono::Array<bool>>* ItemsThatCountAsBombsForDemolitionistToSpawn;
    Field<Mono::Array<bool>>* ItemsThatShouldNotBeInInventory;
    Field<Mono::Array<bool>>* DrawUnsafeIndicator;
    Field<Mono::Array<bool>>* UsesBetterMeleeItemLocation;
    Field<Mono::Array<bool>>* WorksInVoidBag;
    Field<Mono::Array<bool>>* GrassSeeds;
    Field<Mono::Array<int>>* ShimmerTransformToItem;
    Field<Mono::Array<int>>* ShimmerCountsAsItem;
    Field<Mono::Array<int>>* OverflowProtectionTimeOffset;
    Field<Mono::Array<int>>* ItemsForStuffCannon;
    Field<Mono::Array<bool>>* CanBeQuickusedOnGamepad;
    Field<Mono::Array<bool>>* ForcesBreaksSleeping;
    Field<Mono::Array<bool>>* SkipsInitialUseSound;
    Field<Mono::Array<bool>>* UsesCursedByPlanteraTooltip;
    Field<Mono::Array<bool>>* IsAKite;
    Field<Mono::Array<bool>>* ForceConsumption;
    Field<Mono::Array<bool>>* HasAProjectileThatHasAUsabilityCheck;
    Field<Mono::Array<bool>>* CanGetPrefixes;
    Field<Mono::List<int>>* NonColorfulDyeItems;
    Field<Mono::Array<bool>>* ColorfulDyeValues;
    Field<Mono::Array<bool>>* IsAMaterial;
    Field<Mono::Array<int>>* IsCrafted;
    Field<Mono::Array<int>>* IsCraftedCrimson;
    Field<Mono::Array<int>>* IsCraftedCorruption;
    Field<Mono::Array<bool>>* IgnoresEncumberingStone;
    Field<Mono::Array<float>>* ToolTipDamageMultiplier;
    Field<Mono::Array<bool>>* IsAPickup;
    Field<Mono::Array<bool>>* IsDrill;
    Field<Mono::Array<bool>>* IsChainsaw;
    Field<Mono::Array<bool>>* IsPaintScraper;
    Field<Mono::Array<bool>>* SummonerWeaponThatScalesWithAttackSpeed;
    Field<Mono::Array<bool>>* IsFood;
    Field<int>* DefaultKillsForBannerNeeded;
    Field<Mono::Array<int>>* KillsToBanner;
    Field<Mono::Array<bool>>* CanFishInLava;
    Field<Mono::Array<bool>>* IsLavaBait;
    Field<Mono::Array<int>>* ItemSpawnDecaySpeed;
    Field<Mono::Array<bool>>* IsFishingCrate;
    Field<Mono::Array<bool>>* IsFishingCrateHardmode;
    Field<Mono::Array<bool>>* CanBePlacedOnWeaponRacks;
    Field<Mono::Array<int>>* TextureCopyLoad;
    Field<Mono::Array<bool>>* TrapSigned;
    Field<Mono::Array<bool>>* Deprecated;
    Field<Mono::Array<bool>>* NeverAppearsAsNewInInventory;
    Field<Mono::Array<bool>>* CommonCoin;
    Field<Mono::Array<bool>>* ItemIconPulse;
    Field<Mono::Array<bool>>* ItemNoGravity;
    Field<Mono::Array<int>>* ExtractinatorMode;
    Field<Mono::Array<int>>* StaffMinionSlotsRequired;
    Field<Mono::Array<bool>>* ExoticPlantsForDyeTrade;
    Field<Mono::Array<bool>>* NebulaPickup;
    Field<Mono::Array<bool>>* AnimatesAsSoul;
    Field<Mono::Array<bool>>* gunProj;
    Field<Mono::Array<int>>* SortingPriorityBossSpawns;
    Field<Mono::Array<int>>* SortingPriorityWiring;
    Field<Mono::Array<int>>* SortingPriorityMaterials;
    Field<Mono::Array<int>>* SortingPriorityExtractibles;
    Field<Mono::Array<int>>* SortingPriorityRopes;
    Field<Mono::Array<int>>* SortingPriorityPainting;
    Field<Mono::Array<int>>* SortingPriorityTerraforming;
    Field<Mono::Array<int>>* GamepadExtraRange;
    Field<Mono::Array<bool>>* GamepadWholeScreenUseRange;
    Field<Mono::Array<float>>* BonusMeleeSpeedMultiplier;
    Field<Mono::Array<bool>>* GamepadSmartQuickReach;
    Field<Mono::Array<bool>>* Yoyo;
    Field<Mono::Array<bool>>* AlsoABuildingItem;
    Field<Mono::Array<bool>>* LockOnIgnoresCollision;
    Field<Mono::Array<int>>* LockOnAimAbove;
    Field<Mono::Array<float>>* LockOnAimCompensation;
    Field<Mono::Array<bool>>* SingleUseInGamepad;
    Field<Mono::Array<bool>>* Torches;
    Field<Mono::Array<bool>>* WaterTorches;
    Field<Mono::Array<short>>* Workbenches;
    Field<Mono::Array<bool>>* BossBag;
    Field<Mono::Array<bool>>* OpenableBag;


    void RegisterApi() {

        //注册API
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ItemsThatAreProcessedAfterNormalContentSample", (uintptr_t)ItemsThatAreProcessedAfterNormalContentSample);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsLavaImmuneRegardlessOfRarity", (uintptr_t)IsLavaImmuneRegardlessOfRarity);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ItemsThatAllowRepeatedRightClick", (uintptr_t)ItemsThatAllowRepeatedRightClick);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ItemsThatCountAsBombsForDemolitionistToSpawn", (uintptr_t)ItemsThatCountAsBombsForDemolitionistToSpawn);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ItemsThatShouldNotBeInInventory", (uintptr_t)ItemsThatShouldNotBeInInventory);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.DrawUnsafeIndicator", (uintptr_t)DrawUnsafeIndicator);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.UsesBetterMeleeItemLocation", (uintptr_t)UsesBetterMeleeItemLocation);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.WorksInVoidBag", (uintptr_t)WorksInVoidBag);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.GrassSeeds", (uintptr_t)GrassSeeds);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ShimmerTransformToItem", (uintptr_t)ShimmerTransformToItem);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ShimmerCountsAsItem", (uintptr_t)ShimmerCountsAsItem);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.OverflowProtectionTimeOffset", (uintptr_t)OverflowProtectionTimeOffset);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ItemsForStuffCannon", (uintptr_t)ItemsForStuffCannon);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.CanBeQuickusedOnGamepad", (uintptr_t)CanBeQuickusedOnGamepad);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ForcesBreaksSleeping", (uintptr_t)ForcesBreaksSleeping);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SkipsInitialUseSound", (uintptr_t)SkipsInitialUseSound);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.UsesCursedByPlanteraTooltip", (uintptr_t)UsesCursedByPlanteraTooltip);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsAKite", (uintptr_t)IsAKite);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ForceConsumption", (uintptr_t)ForceConsumption);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.HasAProjectileThatHasAUsabilityCheck", (uintptr_t)HasAProjectileThatHasAUsabilityCheck);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.CanGetPrefixes", (uintptr_t)CanGetPrefixes);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.NonColorfulDyeItems", (uintptr_t)NonColorfulDyeItems);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ColorfulDyeValues", (uintptr_t)ColorfulDyeValues);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsAMaterial", (uintptr_t)IsAMaterial);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsCrafted", (uintptr_t)IsCrafted);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsCraftedCrimson", (uintptr_t)IsCraftedCrimson);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsCraftedCorruption", (uintptr_t)IsCraftedCorruption);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IgnoresEncumberingStone", (uintptr_t)IgnoresEncumberingStone);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ToolTipDamageMultiplier", (uintptr_t)ToolTipDamageMultiplier);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsAPickup", (uintptr_t)IsAPickup);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsDrill", (uintptr_t)IsDrill);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsChainsaw", (uintptr_t)IsChainsaw);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsPaintScraper", (uintptr_t)IsPaintScraper);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SummonerWeaponThatScalesWithAttackSpeed", (uintptr_t)SummonerWeaponThatScalesWithAttackSpeed);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsFood", (uintptr_t)IsFood);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.DefaultKillsForBannerNeeded", (uintptr_t)DefaultKillsForBannerNeeded);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.KillsToBanner", (uintptr_t)KillsToBanner);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.CanFishInLava", (uintptr_t)CanFishInLava);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsLavaBait", (uintptr_t)IsLavaBait);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ItemSpawnDecaySpeed", (uintptr_t)ItemSpawnDecaySpeed);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsFishingCrate", (uintptr_t)IsFishingCrate);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.IsFishingCrateHardmode", (uintptr_t)IsFishingCrateHardmode);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.CanBePlacedOnWeaponRacks", (uintptr_t)CanBePlacedOnWeaponRacks);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.TextureCopyLoad", (uintptr_t)TextureCopyLoad);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.TrapSigned", (uintptr_t)TrapSigned);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.Deprecated", (uintptr_t)Deprecated);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.NeverAppearsAsNewInInventory", (uintptr_t)NeverAppearsAsNewInInventory);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.CommonCoin", (uintptr_t)CommonCoin);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ItemIconPulse", (uintptr_t)ItemIconPulse);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ItemNoGravity", (uintptr_t)ItemNoGravity);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ExtractinatorMode", (uintptr_t)ExtractinatorMode);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.StaffMinionSlotsRequired", (uintptr_t)StaffMinionSlotsRequired);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.ExoticPlantsForDyeTrade", (uintptr_t)ExoticPlantsForDyeTrade);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.NebulaPickup", (uintptr_t)NebulaPickup);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.AnimatesAsSoul", (uintptr_t)AnimatesAsSoul);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.gunProj", (uintptr_t)gunProj);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SortingPriorityBossSpawns", (uintptr_t)SortingPriorityBossSpawns);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SortingPriorityWiring", (uintptr_t)SortingPriorityWiring);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SortingPriorityMaterials", (uintptr_t)SortingPriorityMaterials);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SortingPriorityExtractibles", (uintptr_t)SortingPriorityExtractibles);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SortingPriorityRopes", (uintptr_t)SortingPriorityRopes);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SortingPriorityPainting", (uintptr_t)SortingPriorityPainting);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SortingPriorityTerraforming", (uintptr_t)SortingPriorityTerraforming);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.GamepadExtraRange", (uintptr_t)GamepadExtraRange);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.GamepadWholeScreenUseRange", (uintptr_t)GamepadWholeScreenUseRange);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.BonusMeleeSpeedMultiplier", (uintptr_t)BonusMeleeSpeedMultiplier);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.GamepadSmartQuickReach", (uintptr_t)GamepadSmartQuickReach);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.Yoyo", (uintptr_t)Yoyo);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.AlsoABuildingItem", (uintptr_t)AlsoABuildingItem);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.LockOnIgnoresCollision", (uintptr_t)LockOnIgnoresCollision);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.LockOnAimAbove", (uintptr_t)LockOnAimAbove);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.LockOnAimCompensation", (uintptr_t)LockOnAimCompensation);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.SingleUseInGamepad", (uintptr_t)SingleUseInGamepad);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.Torches", (uintptr_t)Torches);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.WaterTorches", (uintptr_t)WaterTorches);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.Workbenches", (uintptr_t)Workbenches);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.BossBag", (uintptr_t)BossBag);
        EFModLoader::RegisterApi::RegisterAPI("Terraria.ID.ItemID.Sets.OpenableBag", (uintptr_t)OpenableBag);


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

    void getHookPtr() {
        Sets = BNM::Class("Terraria.ID", "ItemID", BNM::Image("Assembly-CSharp.dll")).GetInnerClass("Sets");
        cctor = Sets.GetMethod(".cctor", 0);
    }




    void* (*old_cctor)(UnityEngine::Object *);
    void* new_cctor(UnityEngine::Object *instance) {
        old_cctor(instance);

        ItemsThatAreProcessedAfterNormalContentSample = new Field<Mono::List<int>>(Sets.GetField("ItemsThatAreProcessedAfterNormalContentSample"));
        IsLavaImmuneRegardlessOfRarity = new Field<Mono::Array<bool>>(Sets.GetField("IsLavaImmuneRegardlessOfRarity"));
        ItemsThatAllowRepeatedRightClick = new Field<Mono::Array<bool>>(Sets.GetField("ItemsThatAllowRepeatedRightClick"));
        ItemsThatCountAsBombsForDemolitionistToSpawn = new Field<Mono::Array<bool>>(Sets.GetField("ItemsThatCountAsBombsForDemolitionistToSpawn"));
        ItemsThatShouldNotBeInInventory = new Field<Mono::Array<bool>>(Sets.GetField("ItemsThatShouldNotBeInInventory"));
        DrawUnsafeIndicator = new Field<Mono::Array<bool>>(Sets.GetField("DrawUnsafeIndicator"));
        UsesBetterMeleeItemLocation = new Field<Mono::Array<bool>>(Sets.GetField("UsesBetterMeleeItemLocation"));
        WorksInVoidBag = new Field<Mono::Array<bool>>(Sets.GetField("WorksInVoidBag"));
        GrassSeeds = new Field<Mono::Array<bool>>(Sets.GetField("GrassSeeds"));
        ShimmerTransformToItem = new Field<Mono::Array<int>>(Sets.GetField("ShimmerTransformToItem"));
        ShimmerCountsAsItem = new Field<Mono::Array<int>>(Sets.GetField("ShimmerCountsAsItem"));
        OverflowProtectionTimeOffset = new Field<Mono::Array<int>>(Sets.GetField("OverflowProtectionTimeOffset"));
        ItemsForStuffCannon = new Field<Mono::Array<int>>(Sets.GetField("ItemsForStuffCannon"));
        CanBeQuickusedOnGamepad = new Field<Mono::Array<bool>>(Sets.GetField("CanBeQuickusedOnGamepad"));
        ForcesBreaksSleeping = new Field<Mono::Array<bool>>(Sets.GetField("ForcesBreaksSleeping"));
        SkipsInitialUseSound = new Field<Mono::Array<bool>>(Sets.GetField("SkipsInitialUseSound"));
        UsesCursedByPlanteraTooltip = new Field<Mono::Array<bool>>(Sets.GetField("UsesCursedByPlanteraTooltip"));
        IsAKite = new Field<Mono::Array<bool>>(Sets.GetField("IsAKite"));
        ForceConsumption = new Field<Mono::Array<bool>>(Sets.GetField("ForceConsumption"));
        HasAProjectileThatHasAUsabilityCheck = new Field<Mono::Array<bool>>(Sets.GetField("HasAProjectileThatHasAUsabilityCheck"));
        CanGetPrefixes = new Field<Mono::Array<bool>>(Sets.GetField("CanGetPrefixes"));
        NonColorfulDyeItems = new Field<Mono::List<int>>(Sets.GetField("NonColorfulDyeItems"));
        ColorfulDyeValues = new Field<Mono::Array<bool>>(Sets.GetField("ColorfulDyeValues"));
        IsAMaterial = new Field<Mono::Array<bool>>(Sets.GetField("IsAMaterial"));
        IsCrafted = new Field<Mono::Array<int>>(Sets.GetField("IsCrafted"));
        IsCraftedCrimson = new Field<Mono::Array<int>>(Sets.GetField("IsCraftedCrimson"));
        IsCraftedCorruption = new Field<Mono::Array<int>>(Sets.GetField("IsCraftedCorruption"));
        IgnoresEncumberingStone = new Field<Mono::Array<bool>>(Sets.GetField("IgnoresEncumberingStone"));
        ToolTipDamageMultiplier = new Field<Mono::Array<float>>(Sets.GetField("ToolTipDamageMultiplier"));
        IsAPickup = new Field<Mono::Array<bool>>(Sets.GetField("IsAPickup"));
        IsDrill = new Field<Mono::Array<bool>>(Sets.GetField("IsDrill"));
        IsChainsaw = new Field<Mono::Array<bool>>(Sets.GetField("IsChainsaw"));
        IsPaintScraper = new Field<Mono::Array<bool>>(Sets.GetField("IsPaintScraper"));
        SummonerWeaponThatScalesWithAttackSpeed = new Field<Mono::Array<bool>>(Sets.GetField("SummonerWeaponThatScalesWithAttackSpeed"));
        IsFood = new Field<Mono::Array<bool>>(Sets.GetField("IsFood"));
        DefaultKillsForBannerNeeded = new Field<int>(Sets.GetField("DefaultKillsForBannerNeeded"));
        KillsToBanner = new Field<Mono::Array<int>>(Sets.GetField("KillsToBanner"));
        CanFishInLava = new Field<Mono::Array<bool>>(Sets.GetField("CanFishInLava"));
        IsLavaBait = new Field<Mono::Array<bool>>(Sets.GetField("IsLavaBait"));
        ItemSpawnDecaySpeed = new Field<Mono::Array<int>>(Sets.GetField("ItemSpawnDecaySpeed"));
        IsFishingCrate = new Field<Mono::Array<bool>>(Sets.GetField("IsFishingCrate"));
        IsFishingCrateHardmode = new Field<Mono::Array<bool>>(Sets.GetField("IsFishingCrateHardmode"));
        CanBePlacedOnWeaponRacks = new Field<Mono::Array<bool>>(Sets.GetField("CanBePlacedOnWeaponRacks"));
        TextureCopyLoad = new Field<Mono::Array<int>>(Sets.GetField("TextureCopyLoad"));
        TrapSigned = new Field<Mono::Array<bool>>(Sets.GetField("TrapSigned"));
        Deprecated = new Field<Mono::Array<bool>>(Sets.GetField("Deprecated"));
        NeverAppearsAsNewInInventory = new Field<Mono::Array<bool>>(Sets.GetField("NeverAppearsAsNewInInventory"));
        CommonCoin = new Field<Mono::Array<bool>>(Sets.GetField("CommonCoin"));
        ItemIconPulse = new Field<Mono::Array<bool>>(Sets.GetField("ItemIconPulse"));
        ItemNoGravity = new Field<Mono::Array<bool>>(Sets.GetField("ItemNoGravity"));
        ExtractinatorMode = new Field<Mono::Array<int>>(Sets.GetField("ExtractinatorMode"));
        StaffMinionSlotsRequired = new Field<Mono::Array<int>>(Sets.GetField("StaffMinionSlotsRequired"));
        ExoticPlantsForDyeTrade = new Field<Mono::Array<bool>>(Sets.GetField("ExoticPlantsForDyeTrade"));
        NebulaPickup = new Field<Mono::Array<bool>>(Sets.GetField("NebulaPickup"));
        AnimatesAsSoul = new Field<Mono::Array<bool>>(Sets.GetField("AnimatesAsSoul"));
        gunProj = new Field<Mono::Array<bool>>(Sets.GetField("gunProj"));
        SortingPriorityBossSpawns = new Field<Mono::Array<int>>(Sets.GetField("SortingPriorityBossSpawns"));
        SortingPriorityWiring = new Field<Mono::Array<int>>(Sets.GetField("SortingPriorityWiring"));
        SortingPriorityMaterials = new Field<Mono::Array<int>>(Sets.GetField("SortingPriorityMaterials"));
        SortingPriorityExtractibles = new Field<Mono::Array<int>>(Sets.GetField("SortingPriorityExtractibles"));
        SortingPriorityRopes = new Field<Mono::Array<int>>(Sets.GetField("SortingPriorityRopes"));
        SortingPriorityPainting = new Field<Mono::Array<int>>(Sets.GetField("SortingPriorityPainting"));
        SortingPriorityTerraforming = new Field<Mono::Array<int>>(Sets.GetField("SortingPriorityTerraforming"));
        GamepadExtraRange = new Field<Mono::Array<int>>(Sets.GetField("GamepadExtraRange"));
        GamepadWholeScreenUseRange = new Field<Mono::Array<bool>>(Sets.GetField("GamepadWholeScreenUseRange"));
        BonusMeleeSpeedMultiplier = new Field<Mono::Array<float>>(Sets.GetField("BonusMeleeSpeedMultiplier"));
        GamepadSmartQuickReach = new Field<Mono::Array<bool>>(Sets.GetField("GamepadSmartQuickReach"));
        Yoyo = new Field<Mono::Array<bool>>(Sets.GetField("Yoyo"));
        AlsoABuildingItem = new Field<Mono::Array<bool>>(Sets.GetField("AlsoABuildingItem"));
        LockOnIgnoresCollision = new Field<Mono::Array<bool>>(Sets.GetField("LockOnIgnoresCollision"));
        LockOnAimAbove = new Field<Mono::Array<int>>(Sets.GetField("LockOnAimAbove"));
        LockOnAimCompensation = new Field<Mono::Array<float>>(Sets.GetField("LockOnAimCompensation"));
        SingleUseInGamepad = new Field<Mono::Array<bool>>(Sets.GetField("SingleUseInGamepad"));
        Torches = new Field<Mono::Array<bool>>(Sets.GetField("Torches"));
        WaterTorches = new Field<Mono::Array<bool>>(Sets.GetField("WaterTorches"));
        Workbenches = new Field<Mono::Array<short>>(Sets.GetField("Workbenches"));
        BossBag = new Field<Mono::Array<bool>>(Sets.GetField("BossBag"));
        OpenableBag = new Field<Mono::Array<bool>>(Sets.GetField("OpenableBag"));


        RegisterApi();


        auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("Assembly-CSharp.dll.Terraria.ID.ItemID.Sets..cctor");
        for (auto hook : hooks) {
            EFModLoader::Redirect::callFunction<void>(reinterpret_cast<void *>(hook));
        }
        return nullptr;
    }

    void RegisterHook() {
        using namespace EFModLoader::RegisterHook::Unity;
        RegisterIHOOK("Assembly-CSharp.dll.Terraria.ID.ItemID.Sets..cctor", cctor, new_cctor, old_cctor);
    }
}