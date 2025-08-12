/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.ProcessProtoFiles
import util.tasks.setupProtobufConformanceResources

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
}

dependencies {
    testImplementation(projects.grpc.grpcCodec)
    testImplementation(projects.protobuf.protobufCore)
}

setupProtobufConformanceResources()
configureLocalProtocGenDevelopmentDependency()

val generatedCodeDir = project.layout.projectDirectory
    .dir("src")
    .dir("test")
    .dir("generated-code")
    .asFile

tasks.withType<BufGenerateTask>().configureEach {
    if (name.endsWith("Test")) {
        outputDirectory.set(generatedCodeDir)
    }
}

protoSourceSets {
    test {
        proto {
            exclude("**/test_messages_proto2.proto")
            exclude("**/test_messages_proto2_editions.proto")
            exclude("**/test_messages_edition2023.proto")
        }
    }
}
