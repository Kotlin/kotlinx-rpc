/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "protoc-gen"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-conventions")
    includeBuild("../gradle-conventions-settings")
}

plugins {
    id("conventions-repositories")
    id("conventions-version-resolution")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":common") // NOT PUBLIC, grpc and protobuf are fat jars
includePublic(":grpc") // protoc-gen-grpc-kotlin-multiplatform
includePublic(":protobuf") // protoc-gen-kotlin-multiplatform
