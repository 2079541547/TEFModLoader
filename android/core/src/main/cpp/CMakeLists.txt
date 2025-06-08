cmake_minimum_required(VERSION 3.22.1)

project("TEFLoader")

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(TEFMOD_API_SRC
        TEFModLoader/src/tefmod-api/base_type.cpp
        TEFModLoader/src/tefmod-api/mod_logger.cpp
        TEFModLoader/src/tefmod-api/debug_tool.cpp
        TEFModLoader/src/tefmod-api/tefmod.cpp
        TEFModLoader/src/tefmod-api/item.cpp
        TEFModLoader/src/tefmod-api/projectile.cpp
)

add_subdirectory(lib/BNM-Android-master)
add_subdirectory(lib/Dobby)
add_subdirectory(lib/efmod)
add_subdirectory(lib/spdlog)

target_link_libraries(BNM
        PUBLIC
        dobby_static
)

add_library(TEFModLoader SHARED
        ${TEFMOD_API_SRC}
        TEFModLoader/src/logger.cpp
        TEFModLoader/src/hook_manager.cpp
        TEFModLoader/src/api_manager.cpp
        TEFModLoader/src/tefmodloader.cpp
        TEFModLoader/src/item_manager.cpp
        TEFModLoader/src/texture_assets.cpp
        TEFModLoader/src/set_factory.cpp
        TEFModLoader/src/initialize_almost_everything.cpp
        TEFModLoader/src/save_player.cpp
        TEFModLoader/src/recipe.cpp
        TEFModLoader/src/projectile_manager.cpp
)

target_include_directories(TEFModLoader
        PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/TEFModLoader/includes/tefmodloader>
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/TEFModLoader/includes/tefmod-api>
)

target_link_libraries(TEFModLoader
        PUBLIC
        android
        log
        efmodloader
        spdlog
        BNM
)

add_library(example1
        SHARED
        TEFModLoader/mod-example/example1.cpp
)

add_library(example2
        SHARED
        TEFModLoader/mod-example/example2.cpp
)

target_include_directories(example1
        PRIVATE
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/TEFModLoader/includes/tefmod-api>
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/lib/efmod/efmod-api>
)

target_include_directories(example2
        PRIVATE
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/TEFModLoader/includes/tefmod-api>
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/lib/efmod/efmod-api>
)