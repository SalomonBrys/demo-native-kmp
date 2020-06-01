import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get

class HasPlatformPlugin : Plugin<Project> {

    class Extension(private val project: Project) {

        private val rootProperties: RootPropertiesPlugin.Extension by lazy {
            (project.extensions.findByName("rootProperties") ?: run {
                project.apply<RootPropertiesPlugin>()
                project.extensions["rootProperties"]
            }) as RootPropertiesPlugin.Extension
        }

        val android: Boolean by lazy { project.findProperty("excludeAndroid") != "true" && rootProperties["local"].exists }
        val wasm: Boolean by lazy { project.findProperty("excludeWasm") != "true" && rootProperties["wasm"].exists }
    }

    override fun apply(target: Project) {
        target.extensions.add("has", Extension(target))
    }

}
