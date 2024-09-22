//
// Created by eternalfuture on 2024/9/22.
//

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <LOG/MainLOGS.hpp>
#include <EFMod/EFMod.hpp>
#include <Hook/RegisterHook.hpp>

namespace RegisterHook {

    vector<hooks> registerHooks;

    void RegisterHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr) {
        // 查找已存在的API记录
        for (auto& hooks : registerHooks) {
            if (hooks.hookName == hookName) {
                MainLOGS::LOG("Warning", "RegisterApi", "RegisterAPI", "API已存在：" + hookName + " 将不进行注册操作");
                return;
            }
        }
        // 如果没有找到，创建新的API记录
        registerHooks.push_back({hookName, Ptr, new_ptr, old_Ptr});
        MainLOGS::LOG("Info", "RegisterHook", "RegisterHOOK", "注册了新的hook：" + hookName);
    }


    void Register() {
        if (registerHooks.empty()) {
            MainLOGS::LOG("Warning", "RegisterHook", "Register", "什么都没有诶٩(๑`^´๑)۶");
            return;
        }

        for (const auto& hooks : registerHooks) {
            if (EFModLoaderAPI::GetEFModLoader().FindHooks(hooks.hookName).empty()) {
                MainLOGS::LOG("Warning", "RegisterHook", "Register", "没有Mod注册的Hook：" + hooks.hookName);
            } else {
                HOOK(hooks.ptr, hooks.new_ptr, hooks.old_ptr);
                MainLOGS::LOG("Info", "RegisterHook", "Register", "已注册Hook：" + hooks.hookName);
            }
        }
        // 清空注册列表，防止重复注册
        registerHooks.clear();
    }

}