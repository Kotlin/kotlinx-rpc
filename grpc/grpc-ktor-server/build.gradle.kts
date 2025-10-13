/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.grpc.grpcServer)
                implementation(libs.ktor.server.core)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.protobuf.protobufCore)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(projects.grpc.grpcClient)
                implementation(projects.grpc.grpcKtorServer)

                implementation(libs.grpc.kotlin.stub)
                implementation(libs.grpc.netty)

                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.test.host)

                runtimeOnly(libs.logback.classic)
            }
        }
    }
}

configureLocalProtocGenDevelopmentDependency()
