/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

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

                implementation(libs.atomicfu)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(projects.grpc.grpcClient)
                implementation(projects.tests.testUtils)
                implementation(projects.tests.testGrpcProtos)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.grpc.netty)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    // Allows stress-testing the grpc-server implementation.
    providers.gradleProperty("kotlinx.rpc.grpc.server.testRepeats").orNull?.let {
        environment("GRPC_SERVER_TEST_REPEATS", it)
    }
}
