/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

// The test data of the compiler plugin compiles against this project and its `api` dependencies.
// See `testDataLibraries` in compiler-plugin/build.gradle.kts.
dependencies {
    api(projects.core)
    api(projects.utils)
    api(projects.krpc.krpcCore)
    api(projects.grpc.grpcCore)
    api(projects.grpc.grpcMarshaller)
    api(projects.protobuf.protobufLite)
    api(projects.tests.compilerPluginTestLibs.compilerPluginProtos)
    api(libs.kotlinx.io.core)
    api(libs.coroutines.core)
}
