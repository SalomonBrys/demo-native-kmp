import org.jetbrains.kotlin.gradle.targets.js.npm.npmProject

plugins {
    `maybe-android-library`
    `has-platform`
    `root-properties`

    kotlin("multiplatform") version "1.3.72"
}

maybeAndroid {
    android {
        compileSdkVersion(28)
        defaultConfig {
            minSdkVersion(21)
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            externalNativeBuild {
                cmake {
                    arguments.addAll(listOf(
                            "-DJAVA_AWT_LIBRARY=NotNeeded",
                            "-DJAVA_JVM_LIBRARY=NotNeeded",
                            "-DJAVA_INCLUDE_PATH2=NotNeeded",
                            "-DJAVA_AWT_INCLUDE_PATH=NotNeeded"
                    ))
                }
            }
        }
        externalNativeBuild {
            cmake {
                setPath("$rootDir/cpp_jni/CMakeLists.txt")
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }

}

kotlin {
    sourceSets["commonMain"].dependencies {
        implementation(kotlin("stdlib-common"))

        implementation(project(":native-base64"))
    }

    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
    }

    jvm {
        compilations["main"].defaultSourceSet.dependencies {
            implementation(kotlin("stdlib-jdk8"))
        }

        compilations["test"].defaultSourceSet.dependencies {
            implementation(kotlin("test-junit"))
        }
    }

    if (has.android) {
        android()
            sourceSets["androidMain"].dependencies {
            implementation(kotlin("stdlib-jdk8"))
        }
        sourceSets["androidTest"].dependencies {
            implementation(kotlin("test-junit"))
            implementation("androidx.test.ext:junit:1.1.1")
            implementation("androidx.test.espresso:espresso-core:3.2.0")
        }
    }

    fun org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.addDetectCInterop() {
        compilations["main"].cinterops.create("cpp_base64") {
            packageName("org.example.nativeb64.cpp.cinterop")
            includeDirs {
                headerFilterOnly(rootDir.resolve("cpp_native/src"))
            }
            tasks[interopProcessingTaskName].dependsOn(":cpp_native:buildBase64Host")
        }
        binaries.all {
            linkTask.binary.linkerOpts.add("-L$rootDir/cpp_native/build/cmake/out/base64Host/lib")
        }
    }

    val nativeClasspathFix = project.findProperty("nativeClasspathFix") as? String
    if (nativeClasspathFix == null) {
        sourceSets.create("allNativeMain") { dependsOn(sourceSets["commonMain"]) }
        sourceSets.create("allNativeTest") { dependsOn(sourceSets["commonTest"]) }
    }
    fun org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.configureSourceSet() {
        val mainSourceSet = compilations["main"].defaultSourceSet
        val testSourceSet = compilations["test"].defaultSourceSet
        when (nativeClasspathFix) {
            null -> {
                mainSourceSet.dependsOn(sourceSets["allNativeMain"])
                testSourceSet.dependsOn(sourceSets["allNativeTest"])
            }
            name -> {
                mainSourceSet.kotlin.srcDir("src/allNativeMain/kotlin")
                testSourceSet.kotlin.srcDir("src/allNativeTest/kotlin")
            }
            else -> {
                mainSourceSet.dependsOn(sourceSets["${nativeClasspathFix}Main"])
                testSourceSet.dependsOn(sourceSets["${nativeClasspathFix}Test"])
            }
        }
    }

    linuxX64 {
        configureSourceSet()
        addDetectCInterop()
    }

    if (has.wasm) {
        js {
            browser {
            }

            compilations["main"].defaultSourceSet.dependencies {
                implementation(kotlin("stdlib-js"))
            }

            compilations["test"].defaultSourceSet.dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

val currentOs = org.gradle.internal.os.OperatingSystem.current()!!

tasks.withType<org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest> {
    dependsOn(":cpp_jni:buildJni-${currentOs.familyName}")

    systemProperty("java.library.path", rootDir.resolve("cpp_jni/build/cmake/out/jni-${currentOs.familyName}/lib").absolutePath)
}

afterEvaluate {
    tasks.withType<com.android.build.gradle.tasks.factory.AndroidUnitTest>().all {
        enabled = false
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest> {
    dependsOn(":cpp_native:buildBase64Wasm")
    doFirst {
        copy {
            from(rootDir.resolve("cpp_native/build/cmake/out/base64Wasm/js/cpp_base64_js.wasm"))
            into(compilation.npmProject.dir)
        }
    }
}
