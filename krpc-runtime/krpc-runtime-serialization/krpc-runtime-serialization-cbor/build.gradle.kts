import org.jetbrains.krpc.buildutils.kmp

plugins {
    kotlin("multiplatform")
}

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime:krpc-runtime-serialization"))
                api("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.6.0")
            }
        }
    }
}
