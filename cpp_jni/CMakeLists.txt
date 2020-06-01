cmake_minimum_required(VERSION 3.4.1)
project(CPP_Base64_JNI)

set(CMAKE_CXX_STANDARD 14)

find_package(JNI REQUIRED)

include_directories(${JNI_INCLUDE_DIRS})

add_subdirectory(../cpp_native EXCLUDE_FROM_ALL cpp_base64)

include_directories(../cpp_native/src)

add_library(cpp_base64_jni SHARED src/base64_jni.cpp)

target_link_libraries(cpp_base64_jni cpp_base64)

install(
    TARGETS cpp_base64_jni
    ARCHIVE DESTINATION lib
    LIBRARY DESTINATION lib
    COMPONENT library
)
