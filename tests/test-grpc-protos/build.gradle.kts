/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.protoImport
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.tests.testProtos)
                api(projects.protobuf.protobuf)
                api(projects.grpc.grpcCore)
                protoImport(project(path = projects.tests.testProtos.path, configuration = "protoElements"))
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}

configureLocalProtocGenDevelopmentDependency("Main")
