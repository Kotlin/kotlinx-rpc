/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.protoImport

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.utils)
                api(projects.grpc.grpcMarshaller)

                api(libs.kotlinx.io.core)

                implementation(libs.kotlin.reflect)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.protobuf.protobufWkt)
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(projects.tests.testProtos)
                protoImport(project(path = projects.tests.testProtos.path, configuration = "importedProtoElements"))
            }
        }

        jvmTest {
            dependencies {
                // Utf8JavaComparisonTest compares the UTF-8 code with protobuf-java.
                implementation(libs.protobuf.javalite)
            }
        }
    }
}

configureLocalProtocGenDevelopmentDependency("Test")
