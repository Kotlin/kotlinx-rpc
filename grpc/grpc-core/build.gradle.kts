/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.utils)
                api(libs.coroutines.core)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.grpc.util)
                implementation(libs.grpc.stub)
                implementation(libs.grpc.protobuf)
                implementation(libs.grpc.kotlin.stub)
                implementation(libs.protobuf.java.util)
                implementation(libs.protobuf.kotlin)
            }
        }
    }
}
