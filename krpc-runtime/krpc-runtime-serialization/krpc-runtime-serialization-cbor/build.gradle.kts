plugins {
    alias(libs.plugins.conventions.kmp)
}

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime:krpc-runtime-serialization"))
                api(libs.serialization.cbor)
            }
        }
    }
}
