/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

val verificationRepositoryDir = rootProject.layout.buildDirectory.dir("verification-repo")
val verificationVersion = rootProject.version.toString()
val verificationTargetSuffix = when {
    System.getProperty("os.name").lowercase().contains("mac") &&
        System.getProperty("os.arch").lowercase().let { it.contains("aarch64") || it.contains("arm64") } -> "macosarm64"
    System.getProperty("os.name").lowercase().contains("mac") -> "macosx64"
    System.getProperty("os.name").lowercase().contains("linux") &&
        System.getProperty("os.arch").lowercase().let { it.contains("aarch64") || it.contains("arm64") } -> "linuxarm64"
    System.getProperty("os.name").lowercase().contains("linux") -> "linuxx64"
    else -> error("Unsupported verification host")
}
val grpcShimPublishedFiles = listOf(
    verificationRepositoryDir.map { repo ->
        repo.file(
            "org/jetbrains/kotlinx/kotlinx-rpc-grpc-core-shim-$verificationTargetSuffix/" +
                "$verificationVersion/" +
                "kotlinx-rpc-grpc-core-shim-$verificationTargetSuffix-$verificationVersion.klib",
        )
    },
    verificationRepositoryDir.map { repo ->
        repo.file(
            "org/jetbrains/kotlinx/kotlinx-rpc-grpc-core-shim-$verificationTargetSuffix/" +
                "$verificationVersion/" +
                "kotlinx-rpc-grpc-core-shim-$verificationTargetSuffix-$verificationVersion-cinterop-libkgrpc.klib",
        )
    },
    verificationRepositoryDir.map { repo ->
        repo.file(
            "org/jetbrains/kotlinx/kotlinx-rpc-annotation-$verificationTargetSuffix/" +
                "$verificationVersion/" +
                "kotlinx-rpc-annotation-$verificationTargetSuffix-$verificationVersion.klib",
        )
    },
)

kotlin {
    when {
        System.getProperty("os.name").lowercase().contains("mac") &&
            System.getProperty("os.arch").lowercase().let { it.contains("aarch64") || it.contains("arm64") } -> macosArm64()
        System.getProperty("os.name").lowercase().contains("mac") -> macosX64()
        System.getProperty("os.name").lowercase().contains("linux") &&
            System.getProperty("os.arch").lowercase().let { it.contains("aarch64") || it.contains("arm64") } -> linuxArm64()
        System.getProperty("os.name").lowercase().contains("linux") -> linuxX64()
        else -> error("Unsupported verification host")
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(files(grpcShimPublishedFiles))
            }
        }
    }
}
