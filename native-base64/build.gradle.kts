plugins {
    kotlin("multiplatform") version "1.3.72"
}

kotlin {

    sourceSets["commonMain"].dependencies {
        implementation(kotlin("stdlib-common"))
    }

    jvm {
        compilations["main"].defaultSourceSet.dependencies {
            implementation(kotlin("stdlib-jdk8"))
        }
    }

    js {
        browser()
        nodejs()

        compilations["main"].defaultSourceSet.dependencies {
            implementation(kotlin("stdlib-js"))
        }
    }

    linuxX64()
    macosX64()
    mingwX64()

}
