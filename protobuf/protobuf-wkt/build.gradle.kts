/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.buf
import kotlinx.rpc.protoc.generate
import kotlinx.rpc.protoc.proto
import kotlinx.rpc.protoc.protoTasks

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.protobuf.protobufApi)
            }

            proto {
                exclude("exclude/**")
            }
        }
    }
}

fun generatedCodeDir(sourceSetName: String): File = layout.projectDirectory
    .dir("src")
    .dir(sourceSetName)
    .dir("generated-code")
    .asFile

rpc {
    protoc {
        buf.generate.comments {
            includeFileLevelComments = false
        }
    }
}

protoTasks.buf.generate.matchingKotlinSourceSet(kotlin.sourceSets.commonMain).configureEach {
    includeWkt = true
    outputDirectory = generatedCodeDir(properties.sourceSetNames.single())
}

configureLocalProtocGenDevelopmentDependency("Main")
