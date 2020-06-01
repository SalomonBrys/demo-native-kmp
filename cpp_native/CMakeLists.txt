cmake_minimum_required(VERSION 3.4.1)
project(CPP_Base64)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_library(cpp_base64 STATIC src/base64.cpp src/base64_c.cpp)

add_executable(cpp_base64_demo EXCLUDE_FROM_ALL src/demo.cpp)
target_link_libraries(cpp_base64_demo cpp_base64)

install(
        TARGETS cpp_base64
        ARCHIVE DESTINATION lib
        LIBRARY DESTINATION lib
)

if (WASM)
    add_executable(cpp_base64_js src/base64.cpp src/base64_c.cpp)
    set_target_properties(
            cpp_base64_js
            PROPERTIES LINK_FLAGS
            "-s WASM=1 -s MODULARIZE=1 -s DISABLE_EXCEPTION_CATCHING=0 -s EXPORT_NAME=cpp_base64_js -s EXPORTED_FUNCTIONS=[_base64_encode,_base64_decode,_base64_max_len] -s EXTRA_EXPORTED_RUNTIME_METHODS=[UTF8ToString]"
    )
    target_compile_options(cpp_base64_js PUBLIC -fexceptions -s DISABLE_EXCEPTION_CATCHING=0)

    install(
            TARGETS cpp_base64_js
            RUNTIME DESTINATION js
    )
    install(FILES ${CMAKE_CURRENT_BINARY_DIR}/cpp_base64_js.wasm DESTINATION js)
endif ()
