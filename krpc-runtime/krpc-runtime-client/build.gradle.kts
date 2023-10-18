import org.jetbrains.krpc.buildutils.*

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
}

configureMppPublication()

kotlin {
    allTargets()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
                implementation("io.github.oshai:kotlin-logging:5.1.0")
            }
        }
    }
}
