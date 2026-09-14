/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.grpc.grpcClient)
                implementation(projects.tests.grpcBenchmarks.protos)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}
