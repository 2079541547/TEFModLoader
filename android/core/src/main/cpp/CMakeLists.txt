cmake_minimum_required(VERSION 3.22.1)

project("TEFLoader")

add_subdirectory(lib/BNM-Android-master)
add_subdirectory(lib/Dobby)
add_subdirectory(lib/EFModLoader)

target_link_libraries(BNM
        PUBLIC
        Dobby
)

add_library(TEFModLoader SHARED
        TEFModLoader/src/TEFModLoader.cpp
        TEFModLoader/src/hook.cpp
        TEFModLoader/src/utility.cpp
        TEFModLoader/src/log.cpp
        TEFModLoader/src/manager.cpp
)

target_include_directories(TEFModLoader
        PUBLIC
        TEFModLoader/includes
)

target_link_libraries(TEFModLoader
        PUBLIC
        android
        log
        EFModLoader
        BNM
)

add_library(auxiliary
        SHARED
        TEFModLoader/auxiliary.cpp
)

target_link_libraries(auxiliary
        BNM
)

add_library(TEFLoader SHARED
        TEFLoader/src/TEFLoader.cpp)

target_include_directories(TEFLoader
        PUBLIC
        TEFLoader/includes
)

target_link_libraries(TEFLoader
        android
        Dobby
        log)