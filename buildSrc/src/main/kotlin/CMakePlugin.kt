import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get


class CMakePlugin : Plugin<Project> {

    private object Default {
        const val androidApi = 21
    }

    @DslMarker
    annotation class CMakeDsl

    @CMakeDsl
    class Extension(private val project: Project) {

        @CMakeDsl
        class Compilation(val conf: CMakeConfigureTask, val build: CMakeBuildTask) {
            fun conf(configure: CMakeConfigureTask.() -> Unit) = conf.configure()
            fun build(configure: CMakeBuildTask.() -> Unit) = build.configure()
        }

        fun compilation(libName: String, configure: Compilation.() -> Unit = {}): Task {

            val conf = project.tasks.create<CMakeConfigureTask>("configure${libName.capitalize()}") {
                this.libName = libName
            }

            val build = project.tasks.create<CMakeBuildTask>("build${libName.capitalize()}") {
                dependsOn(conf)
                this.libName = libName
            }

            Compilation(conf, build).configure()

            return build
        }

        fun androidCompilations(libName: String, plaform: Int = Default.androidApi, configure: Compilation.() -> Unit = {}): Task {
            val has = (project.extensions.findByName("has") ?: run {
                project.apply<HasPlatformPlugin>()
                project.extensions["has"]
            }) as HasPlatformPlugin.Extension

            val buildAll = project.tasks.maybeCreate("build${libName.capitalize()}Android").apply {
                group = "build"
            }

            if (!has.android) return buildAll

            val rootProperties = (project.extensions.findByName("rootProperties") ?: run {
                project.apply<RootPropertiesPlugin>()
                project.extensions["rootProperties"]
            }) as RootPropertiesPlugin.Extension

            val ndkDir = rootProperties["local"].requiredExistingPath("ndk.dir", "Android NDK")

            fun addAndroidTarget(target: String) {
                val buildTask = compilation("${libName}Android-$target") {
                    conf {
                        cmakeOptions {
                            "CMAKE_TOOLCHAIN_FILE:PATH" += "${ndkDir.absolutePath}/build/cmake/android.toolchain.cmake"
                            "ANDROID_NDK:PATH" += "${ndkDir.absolutePath}/"
                            "ANDROID_PLATFORM:STRING" += "android-$plaform"
                            "ANDROID_ABI:STRING" += target
                        }
                    }
                    configure()
                }

                buildAll.dependsOn(buildTask)
            }

            addAndroidTarget("armeabi-v7a")
            addAndroidTarget("arm64-v8a")
            addAndroidTarget("x86")
            addAndroidTarget("x86_64")

            return buildAll
        }

        @CMakeDsl
        class MultiTargets(private val libName: String, private val ext: Extension) {
            private val commonConf= ArrayList<Compilation.() -> Unit>()

            fun common(configure: Compilation.() -> Unit) { commonConf.add(configure) }

            fun target(targetName: String, configure: Compilation.() -> Unit = {}): Task =
                    ext.compilation("${libName}${targetName.capitalize()}") {
                        this@MultiTargets.commonConf.forEach { it() }
                        configure()
                    }

            fun androidTargets(plaform: Int = Default.androidApi, configure: Compilation.() -> Unit = {}): Task =
                    ext.androidCompilations(libName, plaform) {
                        this@MultiTargets.commonConf.forEach { it() }
                        configure()
                    }

        }

        fun multiTargetsCompilations(libName: String, configure: MultiTargets.() -> Unit) {
            MultiTargets(libName, this).configure()
        }
    }

    override fun apply(target: Project) {
        target.extensions.add("cmake", Extension(target))

        val cleanCMake = target.tasks.create<Delete>("cleanCMake") {
            group = "build"
            delete(target.buildDir.resolve("cmake"))
        }

        target.afterEvaluate {
            val clean = target.tasks.maybeCreate("clean").apply { group = "build" }
            clean.dependsOn(cleanCMake)
        }
    }

}
