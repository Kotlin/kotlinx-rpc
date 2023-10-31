pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-atomicfu") {
                useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "kRPC"

include(":concurrent-hash-map")

include(":krpc-codegen:krpc-compiler-plugin")
include(":krpc-codegen:krpc-ksp-plugin")

include(":krpc-codegen:krpc-codegen-tests:krpc-codegen-tests-jvm")
include(":krpc-codegen:krpc-codegen-tests:krpc-codegen-tests-mpp")

include(":krpc-runtime")
include(":krpc-runtime:krpc-runtime-api")
include(":krpc-runtime:krpc-runtime-client")
include(":krpc-runtime:krpc-runtime-server")
include(":krpc-runtime:krpc-runtime-test")

include(":krpc-runtime:krpc-runtime-serialization")
include(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-json")
include(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-cbor")
include(":krpc-runtime:krpc-runtime-serialization:krpc-runtime-serialization-protobuf")

include(":krpc-transport:krpc-transport-ktor")
include(":krpc-transport:krpc-transport-ktor:krpc-transport-ktor-server")
include(":krpc-transport:krpc-transport-ktor:krpc-transport-ktor-client")

includeBuild("samples/krpc-ktor")
