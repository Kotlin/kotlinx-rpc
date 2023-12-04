plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.krpc)
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":krpc-runtime"))
                api(project(":krpc-runtime:krpc-runtime-server"))
                api(project(":krpc-runtime:krpc-runtime-client"))

                implementation(project(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-json"))

                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.test.junit)
            }
        }
    }
}
