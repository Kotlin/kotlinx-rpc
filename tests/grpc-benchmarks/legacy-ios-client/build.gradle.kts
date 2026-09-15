/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.protoc.proto
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.rpc)
}

val sharedKotlinClientSources = layout.projectDirectory.dir("../shared-kotlin-client/src")

group = "org.jetbrains.kotlinx.rpc.benchmarks"
version = "1.0-SNAPSHOT"

kotlin {
    val iosTargets = listOf(
        iosSimulatorArm64(),
    )

    iosTargets.forEach { target: KotlinNativeTarget ->
        target.binaries.executable {
            baseName = "legacy-grpc-benchmark-client"
            entryPoint = "kotlinx.rpc.grpc.benchmarks.client.main"
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(sharedKotlinClientSources.dir("commonMain/kotlin"))

            dependencies {
                implementation(libs.clikt)
                implementation(libs.coroutines.core)
                implementation(libs.kotlinx.rpc.grpc.client)
                implementation(libs.kotlinx.rpc.protobuf)
            }

            proto {
                setSrcDirs(listOf(layout.projectDirectory.dir("../protos/src/commonMain/proto")))
            }
        }

        iosMain {
            kotlin.srcDir(sharedKotlinClientSources.dir("iosMain/kotlin"))
        }
    }
}

rpc {
    protoc()
}
