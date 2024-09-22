cmake_minimum_required(VERSION 3.22.1)


project("TEFModLoader")
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(shadowhook REQUIRED CONFIG)

set(PUBLIC_INCLUDE_DIRS
        BNM/include
        BNM/external/include
)

set(INCLUDE_DIRS
        BNM/include
        BNM/external/include
        BNM/external
        BNM/external/utf8
        BNM/src/BNM_data
)

add_library(
        BNM
        STATIC
        BNM/src/Class.cpp
        BNM/src/ClassesManagement.cpp
        BNM/src/Coroutine.cpp
        BNM/src/Defaults.cpp
        BNM/src/Delegates.cpp
        BNM/src/EventBase.cpp
        BNM/src/Exceptions.cpp
        BNM/src/FieldBase.cpp
        BNM/src/Hooks.cpp
        BNM/src/Image.cpp
        BNM/src/Internals.cpp
        BNM/src/Internals.hpp
        BNM/src/Loading.cpp
        BNM/src/MethodBase.cpp
        BNM/src/MonoStructures.cpp
        BNM/src/PropertyBase.cpp
        BNM/src/RegisterHook.cpp
        BNM/src/UnityStructures.cpp
        BNM/src/Utils.cpp
)


target_include_directories(
        BNM
        PUBLIC
        ${INCLUDE_DIRS}
)



target_link_libraries(
        BNM
        PUBLIC
        log
        shadowhook::shadowhook
)


set_property(TARGET BNM PROPERTY BNM_INCLUDE_DIRECTORIES ${PUBLIC_INCLUDE_DIRS})




set(PUBLIC_INCLUDE_DIRS1
        EFModLoader/include
)

set(INCLUDE_DIRS1
        EFModLoader/include
        EFModLoader/src
)

add_library(
        EFModLoader
        SHARED
        EFModLoader/src/Hook/RegisterHook.cpp
        EFModLoader/src/LoadMain.cpp
        EFModLoader/src/API/register.cpp
        EFModLoader/src/LOG/MainLOGS.cpp
        EFModLoader/src/Loader/Loadcpp.cpp
)


target_include_directories(
        EFModLoader
        PUBLIC
        ${INCLUDE_DIRS1}
)

set_property(TARGET EFModLoader PROPERTY EFModLoader_INCLUDE_DIRECTORIES ${PUBLIC_INCLUDE_DIRS1})


target_link_libraries(
        EFModLoader
        PUBLIC
        BNM
        log
)



add_library(example_mod SHARED
        example_mod/example_mod.cpp
)

target_link_libraries(
        example_mod
        PUBLIC
        log
)