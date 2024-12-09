/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "kotlinx-rpc"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("gradle-conventions-settings")
    includeBuild("gradle-conventions")

    includeBuild("gradle-plugin")

    apply(from = "gradle-conventions-settings/src/main/kotlin/conventions-repositories.settings.gradle.kts")

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-atomicfu") {
                useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
            }
        }
    }
}

plugins {
    id("conventions-repositories")
    id("conventions-version-resolution")
    id("conventions-develocity")
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    includeBuild("compiler-plugin")
}

includePublic(":protobuf-plugin")

include(":grpc")
includePublic(":grpc:grpc-core")

includePublic(":bom")

includePublic(":utils")

includePublic(":core")

include(":krpc")
includePublic(":krpc:krpc-core")
includePublic(":krpc:krpc-client")
includePublic(":krpc:krpc-server")
includePublic(":krpc:krpc-logging")
includePublic(":krpc:krpc-test")

include(":krpc:krpc-serialization")
includePublic(":krpc:krpc-serialization:krpc-serialization-core")
includePublic(":krpc:krpc-serialization:krpc-serialization-json")
includePublic(":krpc:krpc-serialization:krpc-serialization-cbor")
includePublic(":krpc:krpc-serialization:krpc-serialization-protobuf")

include(":krpc:krpc-ktor")
includePublic(":krpc:krpc-ktor:krpc-ktor-core")
includePublic(":krpc:krpc-ktor:krpc-ktor-server")
includePublic(":krpc:krpc-ktor:krpc-ktor-client")

include(":tests")
include(":tests:compiler-plugin-tests")

include(":jpms-check")
