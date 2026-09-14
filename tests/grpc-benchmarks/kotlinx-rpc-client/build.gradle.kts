/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

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
            dependencies {
                implementation(libs.clikt)
                implementation(libs.coroutines.core)
                implementation(projects.grpc.grpcClient)
                implementation(projects.tests.grpcBenchmarks.protos)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        jvmMain {
            dependencies {
                runtimeOnly(libs.grpc.netty)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}
