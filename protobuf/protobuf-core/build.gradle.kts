/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import util.configureCLibCInterop

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_1
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.utils)
                api(projects.protobuf.protobufInputStream)
                api(projects.grpc.grpcCodec)

                api(libs.kotlinx.io.core)
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

    configureCLibCInterop(project, ":protowire_static") { cinteropCLib ->
        @Suppress("unused")
        val libprotowire by creating  {
            includeDirs(
                cinteropCLib.resolve("include")
            )
            extraOpts(
                "-libraryPath", "${cinteropCLib.resolve("bazel-out/darwin_arm64-opt/bin")}",
            )
        }
    }
}

protoSourceSets {
    commonTest {
        proto {
            exclude("exclude/**")
        }
    }
}

configureLocalProtocGenDevelopmentDependency("Main", "Test")

val generatedCodeDir = layout.projectDirectory
    .dir("src")
    .dir("commonMain")
    .dir("generated-code")
    .asFile

tasks.withType<BufGenerateTask>().configureEach {
    if (name.contains("Main")) {
        includeWkt = true
        outputDirectory = generatedCodeDir
    }
}
