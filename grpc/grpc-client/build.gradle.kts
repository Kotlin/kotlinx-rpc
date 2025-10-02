/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)

    // TODO: Check if we can drop these plugins
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.serialization) // for tests
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.grpc.grpcCore)

                implementation(libs.atomicfu)
                implementation(libs.kotlinx.io.core)
            }
        }

        jvmMain {
            dependencies {
                // TODO: Check if we can drop the API dependencies
                api(libs.grpc.api)
                api(libs.grpc.util)
                api(libs.grpc.stub)
                api(libs.grpc.protobuf)
                api(libs.grpc.protobuf.lite)
                implementation(libs.grpc.kotlin.stub) // causes problems to jpms if api
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.grpc.netty)
            }
        }

        nativeMain {
            dependencies {
                // required for status.proto
                implementation(projects.protobuf.protobufCore)
            }
        }
    }
}
