import org.jetbrains.krpc.buildutils.configureMppPublication
import org.jetbrains.krpc.buildutils.kmp

plugins {
    kotlin("multiplatform")
}

configureMppPublication()

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime:krpc-runtime-client"))
                api(project(":krpc-transport:krpc-transport-ktor"))
                api(project(":krpc-runtime:krpc-runtime-serialization"))

                api("io.ktor:ktor-client-core:2.3.4")
                api("io.ktor:ktor-client-websockets:2.3.4")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
            }
        }
    }
}
