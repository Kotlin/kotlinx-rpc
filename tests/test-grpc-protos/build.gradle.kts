/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.proto
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import java.nio.file.Path as JavaPath

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

val globalRootDir: String by project.extra

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.tests.testProtos)
                api(projects.protobuf.protobuf)
                api(projects.grpc.grpcCore)
            }

            proto {
                // hack until proper imports are done: KRPC-238
                fileImports.from(
                    JavaPath.of(globalRootDir, "tests", "test-protos", "src", "commonMain", "proto", "all_primitives.proto"),
                    JavaPath.of(globalRootDir, "tests", "test-protos", "src", "commonMain", "proto", "enum.proto"),
                    JavaPath.of(globalRootDir, "tests", "test-protos", "src", "commonMain", "proto", "nested.proto"),
                    JavaPath.of(globalRootDir, "tests", "test-protos", "src", "commonMain", "proto", "one_of.proto"),
                    JavaPath.of(globalRootDir, "tests", "test-protos", "src", "commonMain", "proto", "optional.proto"),
                    JavaPath.of(globalRootDir, "tests", "test-protos", "src", "commonMain", "proto", "reference.proto"),
                    JavaPath.of(globalRootDir, "tests", "test-protos", "src", "commonMain", "proto", "reference_package.proto"),
                    JavaPath.of(globalRootDir, "tests", "test-protos", "src", "commonMain", "proto", "repeated.proto"),
                    JavaPath.of(globalRootDir, "tests", "test-protos", "src", "commonMain", "proto", "test_map.proto"),
                )
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}

configureLocalProtocGenDevelopmentDependency("Main")
