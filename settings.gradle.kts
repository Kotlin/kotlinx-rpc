/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

pluginManagement {
    includeBuild("gradle-settings-conventions")
    includeBuild("gradle-conventions")

    includeBuild("gradle-plugin")

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

rootProject.name = "kotlinx-rpc"

includePublic(":bom")

includePublic(":utils")
includePublic(":utils:utils-service-loader")

includePublic(":compiler-plugin")
includePublic(":ksp-plugin")

includePublic(":runtime")
includePublic(":runtime:runtime-api")
includePublic(":runtime:runtime-logging")
includePublic(":runtime:runtime-client")
includePublic(":runtime:runtime-server")
includePublic(":runtime:runtime-test")

includePublic(":runtime:runtime-serialization")
includePublic(":runtime:runtime-serialization:runtime-serialization-json")
includePublic(":runtime:runtime-serialization:runtime-serialization-cbor")
includePublic(":runtime:runtime-serialization:runtime-serialization-protobuf")

includePublic(":transport")
includePublic(":transport:transport-ktor")
includePublic(":transport:transport-ktor:transport-ktor-server")
includePublic(":transport:transport-ktor:transport-ktor-client")

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
