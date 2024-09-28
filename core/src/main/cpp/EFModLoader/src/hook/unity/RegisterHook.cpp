//
// Created by eternalfuture on 2024/9/28.
//

#include <hook/unity/RegisterHook.hpp>
#include <EFMod/EFMod.hpp>
#include <log.hpp>
#include <BNM/MethodBase.hpp>


namespace EFModLoader::RegisterHook::Unity {

    vector<hooks> registerHooks;
    vector<Ihooks> registerIHooks;

    void RegisterIHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr){
        // 查找已存在的API记录
        for (auto& hooks : registerIHooks) {
            if (hooks.hookName == hookName) {
                EFModLoader::Log::LOG("Warning", "RegisterHook", "unity", "RegisterIHOOK", "Hook已存在：" + hookName + " 将不进行注册操作");
                return;
            }
        }
        // 如果没有找到，创建新的API记录
        registerIHooks.push_back({hookName, Ptr, new_ptr, old_Ptr});
        EFModLoader::Log::LOG("Info", "RegisterHook", "unity", "RegisterIHOOK", "注册了新的hook：" + hookName);
    }

    void RegisterHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr) {
        // 查找已存在的API记录
        for (auto& hooks : registerHooks) {
            if (hooks.hookName == hookName) {
                EFModLoader::Log::LOG("Warning", "RegisterHook", "unity", "RegisterHOOK", "Hook已存在：" + hookName + " 将不进行注册操作");
                return;
            }
        }
        // 如果没有找到，创建新的API记录
        registerHooks.push_back({hookName, Ptr, new_ptr, old_Ptr});
        EFModLoader::Log::LOG("Info", "RegisterHook", "unity", "RegisterHOOK", "注册了新的hook：" + hookName);
    }


    void Register() {
        if (registerHooks.empty() && registerIHooks.empty()) {
            EFModLoader::Log::LOG("Warning", "RegisterHook", "unity", "Register", "什么都没有诶٩(๑`^´๑)۶");
            return;
        }

        if (!registerHooks.empty()) {
            EFModLoader::Log::LOG("Warning", "RegisterHook", "unity", "Register", "正在处理普通Hook...");
            for (const auto& hooks : registerHooks) {
                if (EFModLoaderAPI::GetEFModLoader().FindHooks(hooks.hookName).empty()) {
                    EFModLoader::Log::LOG("Warning", "RegisterHook", "unity", "Register", "没有Mod注册的Hook：" + hooks.hookName);
                } else {
                    HOOK(hooks.ptr, hooks.new_ptr, hooks.old_ptr);
                    EFModLoader::Log::LOG("Info", "RegisterHook", "unity", "Register", "已注册Hook：" + hooks.hookName);
                }
            }
            // 清空注册列表，防止重复注册
            registerHooks.clear();
            EFModLoader::Log::LOG("Info", "RegisterHook", "unity", "Register", "普通Hook已处理完成");
        } else {
            EFModLoader::Log::LOG("Info", "RegisterHook", "unity", "Register", "没有注册普通Hook");
        }

        if (!registerIHooks.empty()){
            EFModLoader::Log::LOG("Warning", "RegisterHook", "unity", "Register", "正在处理虚拟Hook...");
            for (const auto& hooks : registerIHooks) {
                if (EFModLoaderAPI::GetEFModLoader().FindHooks(hooks.hookName).empty()) {
                    EFModLoader::Log::LOG("Warning", "RegisterHook", "unity", "Register", "没有Mod注册的Hook：" + hooks.hookName);
                } else {
                    BNM::InvokeHook(hooks.ptr, hooks.new_ptr, hooks.old_ptr);
                    EFModLoader::Log::LOG("Info", "RegisterHook", "unity", "Register", "已注册虚拟Hook：" + hooks.hookName);
                }
            }
            // 清空注册列表，防止重复注册
            registerHooks.clear();
            EFModLoader::Log::LOG("Info", "RegisterHook", "unity", "Register", "虚拟Hook已处理完成");
        } else {
            EFModLoader::Log::LOG("Info", "RegisterHook", "unity", "Register", "没有注册虚拟Hook");
        }


    }

}
