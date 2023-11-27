plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.ksp)
    alias(libs.plugins.krpc)
}

kmp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
                implementation(libs.kotlin.logging)

                implementation(project(":krpc-runtime:krpc-runtime-client"))
                implementation(project(":krpc-runtime:krpc-runtime-server"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

