cmake_minimum_required(VERSION 3.22.1)

project("Loader")

add_subdirectory(BNM-Android-master)
add_subdirectory(EFModLoader)
add_subdirectory(Dobby)

target_link_libraries(BNM
        PUBLIC
        Dobby
)

add_library(${CMAKE_PROJECT_NAME} SHARED
        core.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        EFModLoader
        BNM
        log)

add_library(example_mod_1
    SHARED
        example_mod/example_1.cpp
)

target_link_libraries(example_mod_1
        android
        log
        BNM
)
