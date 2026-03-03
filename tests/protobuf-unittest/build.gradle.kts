/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.buf
import kotlinx.rpc.protoc.generate
import kotlinx.rpc.protoc.kotlinMultiplatform
import kotlinx.rpc.protoc.proto
import kotlinx.rpc.protoc.protoTasks
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import util.tasks.setupProtobufUnittestProtos

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(projects.protobuf.protobufCore)
}

setupProtobufUnittestProtos()
configureLocalProtocGenDevelopmentDependency("Main")

// Only use the protobuf plugin (no gRPC generation needed for unittest protos)
// Add the extracted proto files from build directory as a proto source
sourceSets.main.get().proto {
    srcDir(layout.buildDirectory.dir("protobuf-unittest-protos"))

    // Import non-WKT dependencies (e.g., cpp_features.proto) without generating code for them
    fileImports.from(layout.buildDirectory.dir("protobuf-unittest-imports"))

    exclude(
        // buf incompatibilities:
        "**/unittest_custom_options.proto", // buf: legacy 'message set wire format' (proto1)
        "**/unittest_lite_edition_2024.proto", // buf: edition "2024" not yet supported
        // JVM limit:
        "**/unittest_enormous_descriptor.proto", // generated method exceeds 64KB
        // Generator limitation: proto3 implicit presence generates nullable types instead of non-nullable
        "**/unittest_no_field_presence.proto",
    )

    plugins.empty()
    plugin(rpc.protoc.get().plugins.kotlinMultiplatform)
}
