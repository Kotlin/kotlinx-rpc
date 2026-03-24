/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
}

val verificationRepositoryDir = file("<verification-repo-dir>")
val verificationVersion = "<grpc-shim-version>"
val verificationTargetSuffix = "<host-publication-suffix>"
val grpcShimPublishedFiles = listOf(
    verificationRepositoryDir.resolve(
        "org/jetbrains/kotlinx/kotlinx-rpc-grpc-core-shim-$verificationTargetSuffix/" +
            "$verificationVersion/" +
            "kotlinx-rpc-grpc-core-shim-$verificationTargetSuffix-$verificationVersion.klib",
    ),
    verificationRepositoryDir.resolve(
        "org/jetbrains/kotlinx/kotlinx-rpc-grpc-core-shim-$verificationTargetSuffix/" +
            "$verificationVersion/" +
            "kotlinx-rpc-grpc-core-shim-$verificationTargetSuffix-$verificationVersion-cinterop-grpcCoreInterop.klib",
    ),
    verificationRepositoryDir.resolve(
        "org/jetbrains/kotlinx/kotlinx-rpc-grpc-core-shim-annotation-$verificationTargetSuffix/" +
            "$verificationVersion/" +
            "kotlinx-rpc-grpc-core-shim-annotation-$verificationTargetSuffix-$verificationVersion.klib",
    ),
)

kotlin {
    <host-target-declaration>

    applyDefaultHierarchyTemplate()

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(files(grpcShimPublishedFiles))
            }
        }
    }
}
