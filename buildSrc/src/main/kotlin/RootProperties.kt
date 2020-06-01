import org.gradle.api.Project
import java.io.File
import java.util.*

class RootProperties(val exists: Boolean, private val name: String, private val props: Properties) {

    private fun error(key: String, desc: String): Nothing =
            if (!exists)
                error("Please create root $name.properties and add key $key ($desc).")
            else
                error("Please add key $key in root $name.properties ($desc).")

    fun optionalString(key: String): String? = props.getProperty(key)
    fun requiredString(key: String, desc: String): String = props.getProperty(key) ?: error(key, desc)

    fun optionalExistingPath(key: String): File? = props.getProperty(key)?.let { File(it) } ?.takeIf { it.exists() }
    fun requiredExistingPath(key: String, desc: String): File {
        val path = File(props.getProperty(key) ?: error(key, desc))
        if (!path.exists()) error("Path ${path.absolutePath} defined by key $key in root $name.properties does not exists ($desc).")
        return path
    }

    companion object {
        internal fun from(project: Project, name: String): RootProperties {
            val props = Properties()

            val file = project.rootProject.file("$name.properties").takeIf { it.exists() }
            file?.inputStream()?.use { props.load(it) }

            return RootProperties(file != null, name, props)
        }
    }
}
