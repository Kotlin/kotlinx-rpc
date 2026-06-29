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
        // buf is behind on built-in WKT types
        "**/unittest_features.proto",
        "**/unittest_custom_features.proto",
        // buf: depends on the excluded unittest_custom_options.proto (file_opt1/message_opt1/field_opt1)
        "**/unittest_import_option.proto",
        // buf: rejects the legacy-named oneof fields despite features.enforce_naming_style = STYLE_LEGACY
        "**/unittest_preserve_unknown_enum.proto",
        "**/unittest_preserve_unknown_enum2.proto",
        // JVM limit:
        "**/unittest_enormous_descriptor.proto", // generated method exceeds 64KB
    )

    plugins.empty()
    plugin(rpc.protoc.get().plugins.kotlinMultiplatform)
}

rpc.protoc {
    buf.generate {
        optionalFieldOrNullGetters = true
        comments.includeFileLevelComments = false
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    enabled = false
}
