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

    // Exclude protos incompatible with buf, exceeding JVM limits, or triggering generator bugs:
    exclude(
        // buf incompatibilities:
        "**/unittest_custom_options.proto", // legacy 'message set wire format' (proto1 feature)
        "**/unittest_lite_edition_2024.proto", // edition "2024" not yet supported
        // JVM method size limit:
        "**/unittest_enormous_descriptor.proto", // generated method exceeds 64KB
        // Generator bugs (incorrect code generation for complex proto2/edition features):
        "**/unittest.proto", // groups, extensions, nested types — many codegen issues
        "**/edition_unittest.proto", // edition 2023 equivalent of unittest.proto
        "**/unittest_optimize_for.proto", // depends on unittest.proto
        "**/unittest_embed_optimize_for.proto", // depends on unittest_optimize_for.proto
        "**/unittest_no_field_presence.proto", // depends on unittest.proto
        "**/unittest_lite_imports_nonlite.proto", // depends on unittest.proto
        "**/map_unittest.proto", // depends on unittest.proto
    )

    plugins.empty()
    plugin(rpc.protoc.get().plugins.kotlinMultiplatform)
}
