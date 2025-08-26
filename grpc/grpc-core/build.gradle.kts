/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import util.configureCLibCInterop
import util.configureCLibDependency

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
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
                api(projects.core)
                api(projects.utils)
                api(libs.coroutines.core)
                api(projects.grpc.grpcCodec)

                implementation(libs.atomicfu)
                implementation(libs.kotlinx.io.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(libs.atomicfu)
                implementation(libs.serialization.json)

                implementation(projects.grpc.grpcCodecKotlinxSerialization)
                implementation(projects.protobuf.protobufCore)
            }
        }

        jvmMain {
            dependencies {
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

    configureCLibCInterop(project, ":kgrpc") { cLibSource, cLibOutDir ->
        val grpcPrebuiltDir = cLibSource.resolve("prebuilt-deps/grpc_fat")

        @Suppress("unused")
        val libkgrpc by creating {
            includeDirs(
                cLibSource.resolve("include"),
                cLibSource.resolve("$grpcPrebuiltDir/include"),
                cLibSource.resolve("bazel-cinterop-c/external/grpc+/include"),
            )
            extraOpts("-libraryPath", "$grpcPrebuiltDir")
            extraOpts("-libraryPath", "$cLibOutDir")
        }
    }

    configureCLibDependency(
        project,
        bazelTask = "//prebuilt-deps/grpc_fat:grpc_fat"
    )

}

configureLocalProtocGenDevelopmentDependency()
