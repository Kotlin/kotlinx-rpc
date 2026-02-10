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
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import util.configureCLibCInterop

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
//    id("org.barfuin.gradle.taskinfo") version "2.2.0"
}

kotlin {
    // time API
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_1
        languageVersion = KotlinVersion.KOTLIN_2_1
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.utils)
                api(projects.grpc.grpcCodec)

                api(libs.kotlinx.io.core)
            }

            proto {
                exclude("exclude/**")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.protobuf.java.util)
            }
        }

        nativeMain {
            dependencies {
                implementation(libs.kotlinx.collections.immutable)
            }
        }
    }

    configureCLibCInterop(project, ":protowire_fat") { cLibSource, cLibOutDir ->
        @Suppress("unused")
        val libprotowire by creating {
            includeDirs(
                cLibSource.resolve("include")
            )
            extraOpts("-libraryPath", "$cLibOutDir")
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

configureLocalProtocGenDevelopmentDependency("Main", "Test")
