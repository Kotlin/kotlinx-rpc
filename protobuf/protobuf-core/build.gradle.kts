/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import util.configureCLibCInterop

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.utils)
                api(libs.kotlinx.io.core)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.grpc.grpcCodec)

                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
        }

        jvmMain {
            dependencies {
                api(libs.protobuf.java.util)
                implementation(libs.protobuf.kotlin)
            }
        }

        nativeMain {
            dependencies {
                implementation(libs.kotlinx.collections.immutable)
            }
        }
    }

    configureCLibCInterop(project, ":protowire_static") { cinteropCLib ->
        @Suppress("unused")
        val libprotowire by creating  {
            includeDirs(
                cinteropCLib.resolve("include")
            )
            extraOpts(
                "-libraryPath", "${cinteropCLib.resolve("bazel-out/darwin_arm64-opt/bin")}",
            )
        }
    }
}

protoSourceSets {
    commonTest {
        proto {
            exclude("exclude/**")
        }
    }
}

configureLocalProtocGenDevelopmentDependency()
