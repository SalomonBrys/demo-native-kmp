import org.gradle.api.Plugin
import org.gradle.api.Project


class RootPropertiesPlugin : Plugin<Project> {

    class Extension(private val project: Project) {
        private val props = HashMap<String, RootProperties>()

        operator fun get(name: String): RootProperties = props.getOrPut(name) { RootProperties.from(project, name) }

        fun String.invoke(block: RootProperties.() -> Unit) = get(this).block()
    }

    override fun apply(target: Project) {
        target.extensions.add("rootProperties", Extension(target))
    }

}
