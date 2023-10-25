import org.jetbrains.krpc.buildutils.allTargets
import org.jetbrains.krpc.buildutils.configureMppPublication
import org.jetbrains.krpc.buildutils.kmp

plugins {
    kotlin("multiplatform")
}

configureMppPublication()

kmp(js = false, native = false) {
    sourceSets {

        val jvmMain by getting {
            dependencies {
                api(project(":krpc-runtime:krpc-runtime-server"))
                api(project(":krpc-transport:krpc-transport-ktor"))

                api("io.ktor:ktor-server-core:2.3.4")
                api("io.ktor:ktor-server-websockets:2.3.4")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
            }
        }
    }
}
