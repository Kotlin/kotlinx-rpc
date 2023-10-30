import org.jetbrains.krpc.buildutils.kmp

plugins {
    kotlin("multiplatform")
}

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
            }
        }
    }
}
