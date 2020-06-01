import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get

class MaybeAndroidLibraryPlugin : Plugin<Project> {

    class MaybeAndroid(val android: LibraryExtension?) {
        fun android(block: LibraryExtension.() -> Unit) {
            android?.apply(block)
        }
    }

    override fun apply(target: Project) {
        val has = (target.extensions.findByName("has") ?: run {
            target.apply<HasPlatformPlugin>()
            target.extensions["has"]
        }) as HasPlatformPlugin.Extension

        if (has.android) {
            target.apply { plugin("com.android.library") }
            target.extensions.add("maybeAndroid", MaybeAndroid(target.extensions["android"] as LibraryExtension))
        } else {
            target.extensions.add("maybeAndroid", MaybeAndroid(null))
        }
    }

}
