/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

pluginManagement {
    includeBuild("gradle-settings-conventions")
    includeBuild("gradle-conventions")

    includeBuild("krpc-gradle-plugin")

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
    id("settings-conventions")
    id("compiler-specific-modules")
}

rootProject.name = "kRPC"

include(":krpc-bom")

include(":krpc-utils")
include(":krpc-utils:krpc-utils-service-loader")

include(":krpc-compiler-plugin")
include(":krpc-ksp-plugin")

include(":krpc-runtime")
include(":krpc-runtime:krpc-runtime-api")
include(":krpc-runtime:krpc-runtime-logging")
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

include(":tests:codegen-tests:codegen-tests-mpp")
include(":tests:codegen-tests:codegen-tests-jvm")

val isCI = System.getenv("TEAMCITY_VERSION")?.let { true } ?: false

// samples build with CI different configs, but may break core build if gradle/kotlin version mismatch with theirs
if (!isCI) {
    includeBuild("samples/ktor-all-platforms-app")
    includeBuild("samples/ktor-web-app")
    includeBuild("samples/ktor-android-app")
    includeBuild("samples/simple-ktor-app")
}
