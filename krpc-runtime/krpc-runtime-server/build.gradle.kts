import org.jetbrains.krpc.buildutils.*

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

configureMppPublication()

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime"))

                implementation(project(":krpc-runtime:krpc-runtime-serialization"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
                implementation("io.github.oshai:kotlin-logging:5.1.0")
            }
        }
    }
}
