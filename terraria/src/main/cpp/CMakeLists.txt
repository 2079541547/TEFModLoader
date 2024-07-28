cmake_minimum_required(VERSION 3.22.1)


project("Major")

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
        BNM/src/Delegates.cpp
        BNM/src/EventBase.cpp
        BNM/src/FieldBase.cpp
        BNM/src/Hooks.cpp
        BNM/src/Image.cpp
        BNM/src/Internals.cpp
        BNM/src/Loading.cpp
        BNM/src/MethodBase.cpp
        BNM/src/MonoStructures.cpp
        BNM/src/PropertyBase.cpp
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



add_library(Major
        SHARED
        native-lib.cpp
)





target_link_libraries(Major
        PUBLIC
        BNM
        log
        android
)
