plugins {
    cmake
}

val currentOs = org.gradle.internal.os.OperatingSystem.current()!!

cmake {
    compilation("jni-${currentOs.familyName}")
}
