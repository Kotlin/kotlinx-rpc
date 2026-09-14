/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.protoc.proto
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.rpc)
}

group = "org.jetbrains.kotlinx.rpc.benchmarks"
version = "1.0-SNAPSHOT"

kotlin {
    val iosTargets = listOf(
        iosSimulatorArm64(),
    )

    iosTargets.forEach { target: KotlinNativeTarget ->
        target.binaries.executable {
            baseName = "legacy-grpc-benchmark-client"
            entryPoint = "kotlinx.rpc.grpc.benchmarks.legacy.main"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.rpc.grpc.client)
                implementation(libs.kotlinx.rpc.protobuf)
            }

            proto {
                setSrcDirs(listOf(layout.projectDirectory.dir("../protos/src/commonMain/proto")))
            }
        }
    }
}

rpc {
    protoc()
}
