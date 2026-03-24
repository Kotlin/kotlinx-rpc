/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "grpc-shim"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../../gradle-conventions")
    includeBuild("../../gradle-conventions-settings")
}

plugins {
    id("conventions-repositories")
    id("conventions-version-resolution")
    id("conventions-develocity")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

includePublic(":core")
includePublic(":annotation")
include(":klib-patcher")
include(":verification-negative")
include(":verification-positive")
include(":verification-scope")

project(":verification-negative").projectDir = file("tests/verification-negative")
project(":verification-positive").projectDir = file("tests/verification-positive")
project(":verification-scope").projectDir = file("tests/verification-scope")
