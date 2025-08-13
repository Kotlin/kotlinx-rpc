/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "kotlinx-rpc"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("gradle-conventions-settings")
    includeBuild("gradle-conventions")

    includeBuild("gradle-plugin")

    apply(from = "gradle-conventions-settings/src/main/kotlin/conventions-repositories.settings.gradle.kts")
}

plugins {
    id("conventions-repositories")
    id("conventions-version-resolution")
    id("conventions-develocity")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    includeBuild("compiler-plugin")
    includeBuild("dokka-plugin")
    includeBuild("protoc-gen")
}

include(":protobuf")
includePublic(":protobuf:protobuf-core")
includePublic(":protobuf:protobuf-input-stream")

include(":grpc")
includePublic(":grpc:grpc-core")
includePublic(":grpc:grpc-ktor-server")
includePublic(":grpc:grpc-codec")
includePublic(":grpc:grpc-codec-kotlinx-serialization")

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
include(":tests:krpc-compatibility-tests")

val kotlinMasterBuild = providers.gradleProperty("kotlinx.rpc.kotlinMasterBuild").orNull == "true"

if (!kotlinMasterBuild) {
    include(":tests:compiler-plugin-tests")
}

include(":jpms-check")
