import org.gradle.api.tasks.*
import java.io.File


open class CMakeBuildTask : AbstractExecTask<CMakeBuildTask>(CMakeBuildTask::class.java) {

    @Optional @Input
    var libName: String? = null
        set(value) {
            field = value
            workingDir = project.buildDir.resolve("cmake/build/$libName")
        }

    @Input
    var target: String = "install"

    @InputDirectory
    var sourceDirectory: File = project.projectDir

    @Optional @InputDirectory
    var headerDirectory: File? = null

    @get:OutputDirectory
    val outputDirectory: File
        get() = if (libName != null) project.buildDir.resolve("cmake/out/$libName") else project.buildDir.resolve("cmake/out")

    init {
        group = "build"
        executable = "cmake"
        workingDir = project.buildDir.resolve("cmake/build")
    }

    override fun exec() {
        val a = mutableListOf("--build", ".")
        a.addAll(args ?: emptyList())
        a.addAll(listOf("--target", target))
        setArgs(a)
        super.exec()
    }

}
