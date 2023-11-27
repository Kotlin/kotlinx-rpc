plugins {
    alias(libs.plugins.conventions.kmp)
}

kmp(js = false, native = false) {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":krpc-runtime:krpc-runtime-server"))
                api(project(":krpc-transport:krpc-transport-ktor"))
                api(project(":krpc-runtime:krpc-runtime-serialization"))

                api(libs.ktor.server.core)
                api(libs.ktor.server.websockets)

                implementation(libs.coroutines.core)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}
