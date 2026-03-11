/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.kotlinMultiplatform
import kotlinx.rpc.protoc.proto
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

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(projects.grpc.grpcMarshaller)
}

tasks.test {
    useJUnitPlatform()
}

// The protoc plugin creates proto processing tasks for all source sets, including test.
// Declare the missing dependency so the test proto tasks can find the extracted protos.
tasks.matching { it.name.startsWith("processTest") && it.name.contains("Proto") }.configureEach {
    dependsOn("extractUnittestProtos")
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
        // buf: edition "2024" not yet supported
        "**/edition_unittest.proto",
        "**/map_proto3_unittest.proto",
        "**/unittest_arena.proto",
        "**/unittest_custom_features.proto",
        "**/unittest_custom_options_unlinked.proto",
        "**/unittest_delimited.proto",
        "**/unittest_delimited_import.proto",
        "**/unittest_drop_unknown_fields.proto",
        "**/unittest_features.proto",
        "**/unittest_fuzz_extensions.proto",
        "**/unittest_import_option.proto",
        "**/unittest_lazy_dependencies.proto",
        "**/unittest_lazy_dependencies_custom_option.proto",
        "**/unittest_lazy_dependencies_enum.proto",
        "**/unittest_legacy_features.proto",
        "**/unittest_lite_edition_2024.proto",
        "**/unittest_no_field_presence.proto",
        "**/unittest_preserve_unknown_enum.proto",
        "**/unittest_preserve_unknown_enum2.proto",
        "**/unittest_redaction.proto",
        "**/unittest_string_type.proto",
        "**/unittest_string_view.proto",
        "**/unittest_utf8_string_extensions.proto",
        // JVM limit:
        "**/unittest_enormous_descriptor.proto", // generated method exceeds 64KB
    )

    plugins.empty()
    plugin(rpc.protoc.get().plugins.kotlinMultiplatform)
}

rpc.protoc {
    buf.generate.comments.includeFileLevelComments = false
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    enabled = false
}
