plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.atomicfu)
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
