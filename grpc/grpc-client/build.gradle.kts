/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.grpc.withGrpcClientTestServer

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
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

        nativeMain {
            dependencies {
                implementation(libs.atomicfu)
            }
        }
    }
}

tasks.withType<AbstractTestTask>().configureEach {
    withGrpcClientTestServer()
}
