plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime"))

                implementation(project(":krpc-runtime:krpc-runtime-serialization"))
                implementation(project(":krpc-runtime::krpc-runtime-logging"))

                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}
