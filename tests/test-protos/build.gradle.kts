/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.proto
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.protobuf.protobuf)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}

configureLocalProtocGenDevelopmentDependency("Main")

val protoArchive = tasks.register<Zip>("protoArchive") {
    archiveClassifier.set("proto")
    from(kotlin.sourceSets.commonMain.get().proto.sourceDirectories)
}

val importedProtoArchive = tasks.register<Zip>("importedProtoArchive") {
    archiveClassifier.set("imported-proto")
    from(kotlin.sourceSets.commonMain.get().proto.sourceDirectories) {
        include("to_be_imported.proto")
    }
}

configurations.consumable("protoElements") {
    outgoing.artifact(protoArchive)
}

configurations.consumable("importedProtoElements") {
    outgoing.artifact(importedProtoArchive)
}
