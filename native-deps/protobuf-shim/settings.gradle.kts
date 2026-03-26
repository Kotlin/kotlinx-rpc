/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "protobuf-shim"

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

includePublic(":kotlinx-rpc-protobuf-shim")
project(":kotlinx-rpc-protobuf-shim").projectDir = file("./core")

includePublic(":kotlinx-rpc-protobuf-shim-annotation")
project(":kotlinx-rpc-protobuf-shim-annotation").projectDir = file("./annotation")

include(":klib-patcher")
include(":tests")
