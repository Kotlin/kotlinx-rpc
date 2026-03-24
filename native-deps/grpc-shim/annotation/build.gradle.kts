/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.publish.maven.MavenPublication
import util.registerNativeDependencyTargets

plugins {
    alias(libs.plugins.conventions.kmp)
    id("conventions-publishing")
}

kotlin {
    registerNativeDependencyTargets()
}

publishing {
    publications.withType(MavenPublication::class).configureEach {
        artifactId = when {
            artifactId == "annotation" ->
                "kotlinx-rpc-grpc-core-shim-annotation"
            artifactId.startsWith("annotation-") ->
                artifactId.replaceFirst("annotation", "kotlinx-rpc-grpc-core-shim-annotation")
            artifactId == "kotlinx-rpc-annotation" ->
                "kotlinx-rpc-grpc-core-shim-annotation"
            artifactId.startsWith("kotlinx-rpc-annotation-") ->
                artifactId.replaceFirst("kotlinx-rpc-annotation", "kotlinx-rpc-grpc-core-shim-annotation")
            artifactId == "grpc-core-shim-internal-api" ->
                "kotlinx-rpc-grpc-core-shim-annotation"
            artifactId.startsWith("grpc-core-shim-internal-api-") ->
                artifactId.replaceFirst(
                    "grpc-core-shim-internal-api",
                    "kotlinx-rpc-grpc-core-shim-annotation",
                )
            artifactId == "kotlinx-rpc-grpc-core-shim-internal-api" ->
                "kotlinx-rpc-grpc-core-shim-annotation"
            artifactId.startsWith("kotlinx-rpc-grpc-core-shim-internal-api-") ->
                artifactId.replaceFirst(
                    "kotlinx-rpc-grpc-core-shim-internal-api",
                    "kotlinx-rpc-grpc-core-shim-annotation",
                )
            else -> artifactId
        }
    }
}
