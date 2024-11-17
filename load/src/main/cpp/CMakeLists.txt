cmake_minimum_required(VERSION 3.22.1)

project("TEFModLoader")


set(CMAKE_CXX_STANDARD 23)
set(CMAKE_C_STANDARD 23)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -flto")


set(DOBBY_LIB ${CMAKE_CURRENT_SOURCE_DIR}/EFModLoader/DependencyLibrary/Dobby/${CMAKE_ANDROID_ARCH_ABI}/libdobby.a)


set(BNM_INCLUDE_DIRS
        EFModLoader/DependencyLibrary/BNM/include
        EFModLoader/DependencyLibrary/BNM/external/include
        EFModLoader/DependencyLibrary/BNM/external
        EFModLoader/DependencyLibrary/BNM/external/utf8
        EFModLoader/DependencyLibrary/BNM/src/private
)

add_library(
        BNM
        STATIC
        EFModLoader/DependencyLibrary/BNM/src/Class.cpp
        EFModLoader/DependencyLibrary/BNM/src/ClassesManagement.cpp
        EFModLoader/DependencyLibrary/BNM/src/Coroutine.cpp
        EFModLoader/DependencyLibrary/BNM/src/Defaults.cpp
        EFModLoader/DependencyLibrary/BNM/src/Delegates.cpp
        EFModLoader/DependencyLibrary/BNM/src/EventBase.cpp
        EFModLoader/DependencyLibrary/BNM/src/Exceptions.cpp
        EFModLoader/DependencyLibrary/BNM/src/FieldBase.cpp
        EFModLoader/DependencyLibrary/BNM/src/Hooks.cpp
        EFModLoader/DependencyLibrary/BNM/src/Image.cpp
        EFModLoader/DependencyLibrary/BNM/src/Internals.cpp
        EFModLoader/DependencyLibrary/BNM/src/Loading.cpp
        EFModLoader/DependencyLibrary/BNM/src/MethodBase.cpp
        EFModLoader/DependencyLibrary/BNM/src/MonoStructures.cpp
        EFModLoader/DependencyLibrary/BNM/src/PropertyBase.cpp
        EFModLoader/DependencyLibrary/BNM/src/UnityStructures.cpp
        EFModLoader/DependencyLibrary/BNM/src/Utils.cpp
)

target_include_directories(
        BNM
        PUBLIC
        ${BNM_INCLUDE_DIRS}
        EFModLoader/DependencyLibrary/Dobby
)

target_link_libraries(
        BNM
        PUBLIC
        log
        ${DOBBY_LIB}
)



set(EFMODLOADER_INCLUDE_DIRS
        EFModLoader/include
        EFModLoader/src
)

add_library(
        EFModLoader
        STATIC
        EFModLoader/src/log.cpp
        EFModLoader/src/api/RegisterAPI.cpp
        EFModLoader/src/hook/unity/RegisterHook.cpp
        EFModLoader/src/loader/LoadELFMods.cpp
        EFModLoader/src/android/Android.cpp
        EFModLoader/src/getData.cpp
        EFModLoader/src/android/api.cpp
        EFModLoader/src/agreement.cpp
)

target_include_directories(
        EFModLoader
        PUBLIC
        ${EFMODLOADER_INCLUDE_DIRS}
)

target_link_libraries(
        EFModLoader
        PUBLIC
        log
        android
        BNM
)


set(TEFMODLOADER_INCLUDE_DIRS
        TEFModLoader/include
)

add_library(loader
        SHARED
        TEFModLoader/src/TEFModLoader/UnityEngine/TextAsset.cpp
        TEFModLoader/src/TEFModLoader/Terraria/ID/ItemID.cpp
        TEFModLoader/src/TEFModLoader/Terraria/ID/BuffID.cpp
        TEFModLoader/src/TEFModLoader/Terraria/ID/ProjectileID.cpp
        TEFModLoader/src/TEFModLoader/Terraria/ID/PrefixID.cpp
        TEFModLoader/src/TEFModLoader/Terraria/Main.cpp
        TEFModLoader/src/TEFModLoader/Register.cpp
        TEFModLoader/src/TEFModLoader.cpp
)

target_include_directories(
        loader
        PUBLIC
        ${TEFMODLOADER_INCLUDE_DIRS}
)

target_link_libraries(
        loader
        PUBLIC
        EFModLoader
)


add_library(EFandroid
        SHARED
        EFandroid.cpp
)

target_link_libraries(
        EFandroid
        PUBLIC
        log
        android
)