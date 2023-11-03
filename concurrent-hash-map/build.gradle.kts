import org.jetbrains.krpc.buildutils.kmp

plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu")
}

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime:krpc-runtime-api"))
            }
        }
    }
}
