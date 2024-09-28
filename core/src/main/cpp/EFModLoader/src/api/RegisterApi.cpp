//
// Created by eternalfuture on 2024/9/28.
//

#include <api/Redirect.hpp>
#include <api/RegisterApi.hpp>
#include <EFMod/EFMod.hpp>

namespace EFModLoader::RegisterApi {
    vector<API> registerAPI;

    void RegisterAPI(const string& apiName, uintptr_t api_ptr) {
        // 查找已存在的API记录
        for (auto& api : registerAPI) {
            if (api.apiName == apiName) {
                EFModLoader::Log::LOG("Warning", "RegisterApi", "RegisterAPI", "API已存在：" + apiName + " 将不进行注册操作");
                return;
            }
        }
        // 如果没有找到，创建新的API记录
        registerAPI.push_back({apiName, api_ptr});
        EFModLoader::Log::LOG("Info", "RegisterApi", "RegisterAPI", "注册了新的api：" + apiName);
    }

    void Register() {
        if (registerAPI.empty()) {
            EFModLoader::Log::LOG("Warning", "RegisterApi", "Register", "什么都没有诶٩(๑`^´๑)۶");
            return;
        }

        for (const auto& api : registerAPI) {
            if (EFModLoaderAPI::GetEFModLoader().FindAPIS(api.apiName).empty()) {
                EFModLoader::Log::LOG("Warning", "RegisterApi", "Register", "没有Mod注册的api：" + api.apiName);
            } else {
                for (auto a: EFModLoaderAPI::GetEFModLoader().FindAPIS(api.apiName)) {
                    Redirect::redirectPointer<void*>(a, api.new_ptr);
                }
                EFModLoader::Log::LOG("Info", "RegisterApi", "Register", "已注册api：" + api.apiName);
            }
        }
        // 清空注册列表，防止重复注册
        registerAPI.clear();
    }

}