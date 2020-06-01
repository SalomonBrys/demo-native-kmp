import org.gradle.api.tasks.*
import java.io.File

@Suppress("LeakingThis")
open class CMakeConfigureTask : AbstractExecTask<CMakeConfigureTask>(CMakeConfigureTask::class.java) {

    private val options = CMakeOptions()

    @Optional @Input
    var libName: String? = null
        set(value) {
            field = value
            workingDir = project.buildDir.resolve("cmake/build/$libName")
        }

    @get:InputFile
    internal val cmakeListsTxt: File
        get() = project.file(cmakeProjectPath).resolve("CMakeLists.txt")

    @get:Input
    var cmakeProjectPath: String = project.projectDir.absolutePath
        set(value) {
            field = value
            resetCommandLine()
        }

    @get:OutputDirectory
    val outputDirectory: File get() = workingDir

    @get:Input
    var command: List<String> = listOf("cmake")

    init {
        group = "build"
        workingDir = project.buildDir.resolve("cmake/build")
        resetCommandLine()
    }

    private fun resetCommandLine() {
        setCommandLine(command + options.raw + options.defines.map { "-D${it.key}=${it.value.joinToString(" ")}" } + cmakeProjectPath)
    }

    fun cmakeOptions(builder: CMakeOptions.() -> Unit) {
        options.builder()
        resetCommandLine()
    }

    override fun exec() {
        cmakeOptions {
            "CMAKE_INSTALL_PREFIX:PATH" += if (libName != null) "${project.buildDir.absolutePath}/cmake/out/$libName" else "${project.buildDir.absolutePath}/cmake/out"
        }
        project.mkdir(workingDir)
        super.exec()
    }
}
