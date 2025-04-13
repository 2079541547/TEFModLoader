cmake_minimum_required(VERSION 3.22.1)

project("TEFLoader")

add_subdirectory(lib/BNM-Android-master)
add_subdirectory(lib/Dobby)
add_subdirectory(lib/EFModLoader)

target_link_libraries(BNM
        PUBLIC
        dobby_static
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