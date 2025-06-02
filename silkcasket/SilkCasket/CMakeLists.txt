cmake_minimum_required(VERSION 3.30)
project(SilkCasket)

set(CMAKE_CXX_STANDARD 17)
set(EXAMPLE ON)
set(TEST ON)

set(CPACK_PACKAGE_DESCRIPTION_SUMMARY "LZ4 compression library")
set(LZ4_PATH lib/LZ4/)
include(CPack)

INCLUDE (CheckTypeSize)
check_type_size("void *" SIZEOF_VOID_P)
IF(${SIZEOF_VOID_P} STREQUAL "8")
    set (CMAKE_SYSTEM_PROCESSOR "64bit")
    MESSAGE(STATUS "64 bit architecture detected, size of void * is " ${SIZEOF_VOID_P})
ENDIF()

IF("${CMAKE_C_COMPILER_ID}" STREQUAL "GNU" OR "${CMAKE_C_COMPILER_ID}" STREQUAL "Clang")
    SET(GNU_COMPATIBLE_COMPILER 1)
ENDIF()

if(GNU_COMPATIBLE_COMPILER)
    if(UNIX AND BUILD_LIBS)
        add_definitions(-fPIC)
    endif()
endif()

set(LZ4_SRCS_LIB ${LZ4_PATH}lz4.c ${LZ4_PATH}lz4hc.c ${LZ4_PATH}lz4.h ${LZ4_PATH}lz4hc.h ${LZ4_PATH}lz4frame.c ${LZ4_PATH}lz4frame.h ${LZ4_PATH}xxhash.c)

add_library(liblz4 STATIC ${LZ4_SRCS_LIB})

set_target_properties(liblz4 PROPERTIES
        OUTPUT_NAME lz4
        SOVERSION "${CPACK_PACKAGE_VERSION_MAJOR}.${CPACK_PACKAGE_VERSION_MINOR}"
)

INCLUDE_DIRECTORIES (${LZ4_PATH})



file(GLOB LZMA2FAST_SRC "lib/LZMA2-Fast/*.c")
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/lib/LZMA2-Fast/)
add_library(LZMA2_Fast STATIC ${LZMA2FAST_SRC})

file(GLOB LIZARD_SRC1 "lib/Lizard/xxhash/*.c")
file(GLOB LIZARD_SRC2 "lib/Lizard/entropy/*.c")
file(GLOB LIZARD_SRC3 "lib/Lizard/*.c")
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/lib/Lizard)
add_library(Lizard STATIC ${LIZARD_SRC1} ${LIZARD_SRC2} ${LIZARD_SRC3})


add_library(SilkCasket
        STATIC
        src/structure.cpp
        src/compress.cpp
        src/utils.cpp
        src/build.cpp
        src/encryption.cpp
        src/analysis.cpp
        src/analysis_men.cpp
        src/SilkCasket.cpp
)

target_include_directories(SilkCasket
    PRIVATE
        include/SilkCasket

    PUBLIC
        include
)

target_link_libraries(SilkCasket
    PRIVATE
        liblz4
        LZMA2_Fast
        Lizard
)

if (${EXAMPLE})
    add_executable(example-tool
            example/example-tool.cpp
    )
    target_link_libraries(example-tool
            SilkCasket
    )
endif ()

if (${TEST})
    add_executable(test
            example/test.cpp
    )
    target_link_libraries(test
            SilkCasket
    )
endif ()