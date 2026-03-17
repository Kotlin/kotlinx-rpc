/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.proto
import util.configureCLibCInterop
import java.nio.file.Path as JavaPath

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

val globalRootDir: String by project.extra

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
            }

            proto {
                // hack until proper imports are done: KRPC-238
                fileImports.from(
                    JavaPath.of(
                        globalRootDir,
                        "tests",
                        "test-protos",
                        "src",
                        "commonMain",
                        "proto",
                        "to_be_imported.proto",
                    )
                )
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.protobuf.java.util)
            }
        }

        nativeMain {
            dependencies {
                implementation(libs.kotlinx.collections.immutable)
            }
        }
    }

    configureCLibCInterop(project, ":protowire_fat") { cLibSource, cLibOutDir ->
        @Suppress("unused")
        val libprotowire by creating {
            includeDirs(
                cLibSource.resolve("include")
            )
            extraOpts("-libraryPath", "$cLibOutDir")
        }
    }
}

configureLocalProtocGenDevelopmentDependency("Test")
