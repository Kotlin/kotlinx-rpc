/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.proto
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon
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
                api(projects.protobuf.protobufInputStream)
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

        buf.generate.allTasks().matchingKotlinSourceSet(kotlin.sourceSets.commonMain).configureEach {
            includeWkt = true
            outputDirectory = generatedCodeDir(properties.sourceSetName)
        }
    }
}

configureLocalProtocGenDevelopmentDependency("Main", "Test")

// TODO: What is the correct way to declare this dependency? (KRPC-223)
//  (without it fails when executing "publishAllPublicationsToBuildRepository")
val bufGenerateCommonMain: TaskProvider<Task> = tasks.named("bufGenerateCommonMain")

tasks.withType<org.gradle.jvm.tasks.Jar>().configureEach {
    // Only for sources jars
    if (archiveClassifier.orNull == "sources" || name.endsWith("SourcesJar")) {
        dependsOn(bufGenerateCommonMain)
    }
}

// TODO @Mr3zee: Remove this task dependency once new gralde plugin version merged
tasks.withType<KotlinCompileCommon>().configureEach {
    dependsOn(bufGenerateCommonMain)
}