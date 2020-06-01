plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation("com.android.tools.build:gradle:3.6.3")
}

gradlePlugin {
    plugins {
        register("root-properties-plugin") {
            id = "root-properties"
            implementationClass = "RootPropertiesPlugin"
        }
        register("cmake-plugin") {
            id = "cmake"
            implementationClass = "CMakePlugin"
        }
        register("maybe-android-library-plugin") {
            id = "maybe-android-library"
            implementationClass = "MaybeAndroidLibraryPlugin"
        }
        register("has-platform-plugin") {
            id = "has-platform"
            implementationClass = "HasPlatformPlugin"
        }
    }
}
