#include <iostream>
#include "../SilkCasket.hpp"
#include <jni.h>
#include "json.hpp"


extern "C"
JNIEXPORT void JNICALL
Java_silkways_terraria_efmodloader_logic_efmod_ModManager_install(JNIEnv *env, jobject thiz,
                                                                  jstring inpu_path,
                                                                  jstring out_path) {
        
    auto inputPath = std::filesystem::path(env->GetStringUTFChars(inpu_path, nullptr));
    auto outPath = std::filesystem::path(env->GetStringUTFChars(out_path, nullptr));

    SilkCasket::analysis Analysis(inputPath, "EFMod");
    Analysis.releaseFile("mod.icon", outPath);
        Analysis.releaseFile("mod.json", outPath);
        
        std::ifstream file(outPath / "mod.json");
        nlohmann::json j;
        file >> j;
        if ((bool)j["mod"]["Modx"]) std::filesystem::create_directory(outPath / "Modx");
        file.close();
        
        
        SilkCasket_compress_A_File(false,
                                   outPath / "mod.json",
                                   outPath / "mod",
                                   {false, true, true, true, true}
        );
        
        std::filesystem::remove(outPath / "mod.json");
        std::filesystem::remove_all(outPath / "silk_casket_temp");
        
        Analysis.releaseFolder("lib/android", outPath);
    try { Analysis.releaseFile("page.jar", outPath); } catch (...){}
    try { Analysis.releaseFolder("private", outPath); } catch (...){}
    
    
}


extern "C"
JNIEXPORT jbyteArray JNICALL
Java_silkways_terraria_efmodloader_logic_efmod_ModManager_getModInfo(JNIEnv *env, jobject thiz,
                                                                     jstring inpu_path) {
    auto inputPath = std::filesystem::path(env->GetStringUTFChars(inpu_path, nullptr)) / "mod";
    std::vector<uint8_t> data(get_entry_data(inputPath, "mod.json", "EFMod"));
    jbyteArray result = env->NewByteArray(data.size());
    env->SetByteArrayRegion(result, 0, data.size(), reinterpret_cast<const jbyte*>(&data[0]));
    return result;
}

