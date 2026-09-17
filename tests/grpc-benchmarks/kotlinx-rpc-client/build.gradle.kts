/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import util.targets.configureSwiftPMXcodeBuildConfiguration

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

configureSwiftPMXcodeBuildConfiguration("Release")

val sharedKotlinClientSources = layout.projectDirectory.dir("../shared-kotlin-client/src")

kotlin {
    jvm {
        binaries {
            executable {
                mainClass = "kotlinx.rpc.grpc.benchmarks.client.MainKt"
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.executable {
            baseName = "kotlinx-rpc-grpc-benchmark-client"
            entryPoint = "kotlinx.rpc.grpc.benchmarks.client.main"
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(sharedKotlinClientSources.dir("commonMain/kotlin"))

            dependencies {
                implementation(libs.clikt)
                implementation(libs.coroutines.core)
                implementation(projects.grpc.grpcClient)
                implementation(projects.tests.grpcBenchmarks.protos)
            }
        }

        commonTest {
            kotlin.srcDir(sharedKotlinClientSources.dir("commonTest/kotlin"))

            dependencies {
                implementation(kotlin("test"))
            }
        }

        iosMain {
            kotlin.srcDir(sharedKotlinClientSources.dir("iosMain/kotlin"))
        }

        jvmMain {
            dependencies {
                runtimeOnly(libs.grpc.netty)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}
