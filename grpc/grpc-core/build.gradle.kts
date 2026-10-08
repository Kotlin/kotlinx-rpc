/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import util.grpc.withGrpcClientTestServer

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

    // we must re-apply the default hierarchy template again, because we added custom default source sets below,
    // otherwise it wouldn't be applied.
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.utils)
                api(libs.coroutines.core)
                api(projects.grpc.grpcMarshaller)
                api(libs.kotlinx.io.core)

                implementation(libs.atomicfu)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(libs.atomicfu)
                implementation(libs.serialization.json)

                implementation(projects.grpc.grpcMarshallerKotlinxSerialization)
                implementation(projects.protobuf.protobufLite)
                implementation(projects.grpc.grpcClient)
                implementation(projects.tests.testProtos)
                implementation(projects.tests.testUtils)
            }
        }

        // An intermediate sourceSet that bundles all desktop targets (JVM, macOS and linux)
        // for testing. This allows us to write unit tests that spin up a gRPC server, which is not
        // possible for native mobile targets.
        val desktopTest by creating {
            dependsOn(commonTest.get())

            dependencies {
                implementation(projects.grpc.grpcServer)
            }
        }

        val desktopNativeTest by creating {
            dependsOn(desktopTest)
        }

        jvmMain {
            dependencies {
                api(libs.grpc.api)
                api(libs.grpc.util)
                api(libs.grpc.stub)
            }
        }

        jvmTest {
            dependsOn(desktopTest)

            dependencies {
                implementation(libs.grpc.core)
                implementation(libs.grpc.netty)
            }
        }

        nativeMain {
            dependencies {
                // TODO: Remove this dependency once we remove the protobuf-shim dependency (KRPC-540)
                implementation(projects.protobuf.protobufLite)
                implementation(libs.kotlinx.rpc.grpc.core.shim)
            }
        }

        macosTest {
            dependsOn(desktopNativeTest)
        }

        linuxTest {
            dependsOn(desktopNativeTest)
        }
    }


    // configures linkReleaseTest task to build and link the test binary in RELEASE mode.
    // this can be useful for performance analysis.
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries {
            test(
                buildTypes = listOf(
                    org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE
                )
            )
        }
    }
}

tasks.withType<AbstractTestTask>().configureEach {
    withGrpcClientTestServer()
}
