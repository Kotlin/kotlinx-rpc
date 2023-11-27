plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.atomicfu)
}

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization"))

                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
                implementation(libs.kotlin.logging)
            }
        }
    }
}
