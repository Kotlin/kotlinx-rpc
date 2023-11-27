plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.ksp)
    alias(libs.plugins.krpc)
    alias(libs.plugins.serialization)
}

kmp {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":krpc-runtime:krpc-runtime-server"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-json"))

                implementation(libs.ktor.websockets)
                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(project("krpc-transport-ktor-server"))
                implementation(project("krpc-transport-ktor-client"))

                implementation(libs.kotlin.test)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.client.cio)
            }
        }
    }
}
