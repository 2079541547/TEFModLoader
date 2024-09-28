cmake_minimum_required(VERSION 3.22.1)


project("TEFModLoader")
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -flto")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -flto -std=c++20")

find_package(shadowhook REQUIRED CONFIG)

set(BNM_PUBLIC_INCLUDE_DIRS
        EFModLoader/DependencyLibrary/BNM/include
        EFModLoader/DependencyLibrary/BNM/external/include
)

set(BNM_INCLUDE_DIRS
        EFModLoader/DependencyLibrary/BNM/include
        EFModLoader/DependencyLibrary/BNM/external/include
        EFModLoader/DependencyLibrary/BNM/external
        EFModLoader/DependencyLibrary/BNM/external/utf8
        EFModLoader/DependencyLibrary/BNM/src/BNM_data
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
)

target_link_libraries(
        BNM
        PUBLIC
        shadowhook::shadowhook
        log
)


set(EFMODLOADER_INCLUDE_DIRS
        EFModLoader/include
        EFModLoader/src
)

add_library(
        EFModLoader
        STATIC
        EFModLoader/src/log.cpp
        EFModLoader/src/api/RegisterApi.cpp
        EFModLoader/src/hook/unity/RegisterHook.cpp
        EFModLoader/src/loader/LoadELFMods.cpp
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
        BNM
)

add_library(${CMAKE_PROJECT_NAME}
        SHARED
        TEFModLoader/TEFModLoader.cpp
)

target_link_libraries(
        ${CMAKE_PROJECT_NAME}
        PUBLIC
        EFModLoader
)

add_library(example_mod SHARED
        example_mod/LuckyDamage/example_mod.cpp
)

target_link_libraries(
        example_mod
        PUBLIC
        log
)
