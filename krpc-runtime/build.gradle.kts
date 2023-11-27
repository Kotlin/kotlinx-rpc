plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.krpc)
    alias(libs.plugins.atomicfu)
}

kmp {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":krpc-runtime:krpc-runtime-api"))
                api(project(":krpc-utils"))

                implementation(project(":krpc-runtime:krpc-runtime-serialization"))

                api(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
                implementation(libs.kotlin.logging)
            }
        }
        commonTest {
            dependencies {
                implementation(project(":krpc-runtime:krpc-runtime-client"))
                implementation(project(":krpc-runtime:krpc-runtime-server"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization"))

                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.slf4j.api)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(project(":krpc-runtime:krpc-runtime-test"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-json"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-cbor"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-protobuf"))

                implementation(libs.slf4j.simple)
            }
        }
    }
}
