plugins {
    alias(libs.plugins.conventions.kmp)
}

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime:krpc-runtime-client"))
                api(project(":krpc-transport:krpc-transport-ktor"))
                api(project(":krpc-runtime:krpc-runtime-serialization"))

                api(libs.ktor.client.core)
                api(libs.ktor.client.websockets)

                implementation(libs.coroutines.core)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}
