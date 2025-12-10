/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import util.configureCLibCInterop
import util.configureCLibDependency
import util.doInBackground
import util.registerBuildCLibIncludeDirTask
import util.commandLine

plugins {
    alias(libs.plugins.conventions.kmp)
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
                implementation(projects.grpc.grpcClient)
                implementation(projects.grpc.grpcServer)
                implementation(projects.tests.testProtos)
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


    // configure task to extract the include dir from the gRPC core library
    val grpcIncludeDir = project.layout.buildDirectory.dir("bazel-out/grpc-include")
    val grpcIncludeDirTask = project.registerBuildCLibIncludeDirTask(
        "//prebuilt-deps/grpc_fat:grpc_include_dir",
        grpcIncludeDir
    )

    // configure pre-built gRPC core library
    configureCLibDependency(project, "//prebuilt-deps/grpc_fat:grpc_fat")

    configureCLibCInterop(
        project, ":kgrpc",
        // depends on the grpc include dir
        cinteropTaskDependsOn = listOf(grpcIncludeDirTask)
    ) { cLibSource, cLibOutDir ->
        val grpcPrebuiltDir = cLibSource.resolve("prebuilt-deps/grpc_fat")

        @Suppress("unused")
        val libkgrpc by creating {
            includeDirs(
                cLibSource.resolve("include"),
                cLibSource.resolve("${grpcIncludeDir.get()}/include"),
            )
            extraOpts("-libraryPath", "$grpcPrebuiltDir")
            extraOpts("-libraryPath", "$cLibOutDir")
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

// run gRPC test server (from `test/grpc-test-server` module) background while tests are running
tasks.withType<Test>().configureEach {
    dependsOn(":tests:grpc-test-server:installDist")

    val testServerDir = project(":tests:grpc-test-server").projectDir
    doInBackground {
        commandLine {
            workingDir = testServerDir
            command = "./build/install/grpc-test-server/bin/grpc-test-server"
        }
    }
}
