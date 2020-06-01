plugins {
    cmake
    `root-properties`
}

val currentOs = org.gradle.internal.os.OperatingSystem.current()!!

cmake {
    multiTargetsCompilations("base64") {
        common {
            conf.cmakeOptions {
                "CMAKE_BUILD_TYPE:STRING" += "Release"

                when {
                    currentOs.isLinux -> {
                        "CMAKE_CXX_FLAGS:STRING" += "-D_GLIBCXX_USE_CXX11_ABI=0"
                    }
                    currentOs.isWindows -> {
                        "G" -= "MinGW Makefiles"
                    }
                }
            }
            build {
                sourceDirectory = projectDir.resolve("src")
                args("--config", "Release")
            }
        }

        target("host")

        androidTargets {
            val localProps = rootProperties["local"]
        }

        target("wasm") {
            val wasmProps = rootProperties["wasm"]
            val emsdkDir = wasmProps.requiredExistingPath("emsdk.dir", "Emscripten SDK path")

            conf {
                command = listOf("$emsdkDir/upstream/emscripten/emcmake") + command
                cmakeOptions {
                    "WASM" += "1"
                }
            }

        }

    }

}
