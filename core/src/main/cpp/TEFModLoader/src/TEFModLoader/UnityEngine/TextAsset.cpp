//
// Created by eternalfuture on 2024/10/20.
//

#include <TEFModLoader/UnityEngine/TextAsset.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <EFModLoader/api/RegisterApi.hpp>
#include <EFModLoader/hook/unity/RegisterHook.hpp>
#include <EFModLoader/api/Redirect.hpp>

namespace UnityEngine::TextAsset {

    Class TextAsset;
    MethodBase get_bytes;
    MethodBase get_text;
    BNM::Method<Mono::String *> ToString{};

    MethodBase* ToString_ptr;

    void getHookPtr() {
        TextAsset = Class("UnityEngine", "TextAsset", BNM::Image("UnityEngine.CoreModule.dll"));
        ToString = TextAsset.GetMethod("ToString", 0);
        get_bytes = TextAsset.GetMethod("get_bytes", 0);
        get_text = TextAsset.GetMethod("get_text", 0);
    }

    void RegisterHook() {
        using namespace EFModLoader::RegisterHook::Unity;
        //RegisterHook("UnityEngine.CoreModule.dll.UnityEngine.get_text", get_text, (void *)new_get_text, (void**) old_get_text);
        HOOK(get_text, new_get_text, old_get_text);
        //RegisterIHOOK("UnityEngine.CoreModule.dll.UnityEngine.get_text", get_text, new_get_text, old_get_text);
    }

    Mono::String (*old_get_text)(BNM::UnityEngine::Object *);
    Mono::String new_get_text(BNM::UnityEngine::Object *instance) {

        ToString[instance]();

        BNM_LOG_DEBUG("已劫持获取字符串函数: %s", ToString[instance].str().c_str());

        return old_get_text(instance);
    }


}