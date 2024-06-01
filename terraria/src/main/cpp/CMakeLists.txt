cmake_minimum_required(VERSION 3.22.1)


project("ToolBox")

set(BNM_LINK_LOG OFF)

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

if (${BNM_LINK_LOG})
    target_link_libraries(
            BNM
            PUBLIC
            log
    )
endif ()


set_property(TARGET BNM PROPERTY BNM_INCLUDE_DIRECTORIES ${PUBLIC_INCLUDE_DIRS})

    add_library(toolbox
            SHARED
            toolbox.cpp
    )

    target_link_libraries(
            toolbox
            PUBLIC
            BNM
            log
            android
    )