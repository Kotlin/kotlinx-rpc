/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "kotlinx-rpc"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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

dependencyResolutionManagement {
    includeBuild("compiler-plugin")
    includeBuild("ksp-plugin")
}

includePublic(":bom")

includePublic(":utils")

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
