cmake_minimum_required(VERSION 3.30)

project(silkcasket)

set(CMAKE_CXX_STANDARD 17)


if(MSVC)
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /O2 /Oi /GL")
    set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} /O2 /Oi /GL")

    set(CMAKE_EXE_LINKER_FLAGS_RELEASE "${CMAKE_EXE_LINKER_FLAGS_RELEASE} /LTCG")
elseif(CMAKE_CXX_COMPILER_ID MATCHES "GNU|Clang")
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O3 -march=native -flto")
    set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -O3 -march=native -flto")

    if(CMAKE_CXX_COMPILER_ID STREQUAL "Clang")
        set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -mllvm -inline-threshold=1000")
    endif()
endif()

set(CMAKE_INTERPROCEDURAL_OPTIMIZATION TRUE)

add_subdirectory(SilkCasket-main)

add_library(silkcasket
        SHARED
        main.cpp
)


if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    target_include_directories(silkcasket
            PRIVATE
            jni
            jni/win32
            jni/win32/bridge
    )
elseif (CMAKE_SYSTEM_NAME STREQUAL "Android")
    target_compile_definitions(silkcasket PRIVATE ANDROID_PLATFORM)
else ()
    target_include_directories(silkcasket
            PRIVATE
            jni-linux
    )
endif()



target_link_libraries(silkcasket
    SilkCasket
)

if(CMAKE_SYSTEM_NAME STREQUAL "Android")

if(NOT CMAKE_BUILD_TYPE MATCHES "Debug")
    add_custom_command(TARGET silkcasket POST_BUILD
        COMMAND ${CMAKE_STRIP} $<TARGET_FILE:silkcasket>)
endif()

endif()
