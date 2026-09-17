/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import org.gradle.api.tasks.testing.AbstractTestTask
import util.grpc.withGrpcClientTestServer
import util.targets.configureNonIosSourceSets

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    configureNonIosSourceSets()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.grpc.grpcCore)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(projects.tests.testProtos)
                implementation(projects.tests.testUtils)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.grpc.netty)
            }
        }

        named("nonIosNativeMain") {
            dependencies {
                implementation(libs.atomicfu)
            }
        }

        iosMain {
            dependencies {
                implementation(projects.grpc.grpcSwift)
                implementation(libs.atomicfu)
            }
        }
    }
}

tasks.withType(AbstractTestTask::class.java).configureEach {
    withGrpcClientTestServer()
}
