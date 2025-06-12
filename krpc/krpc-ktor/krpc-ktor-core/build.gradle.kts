/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.krpc.krpcCore)

                implementation(libs.ktor.websockets)
                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.krpc.krpcSerialization.krpcSerializationJson)
                implementation(projects.krpc.krpcKtor.krpcKtorServer)
                implementation(projects.krpc.krpcKtor.krpcKtorClient)
                implementation(projects.krpc.krpcLogging)

                implementation(libs.kotlin.test)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.test.host)
                implementation(libs.ktor.server.websockets)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.client.cio)
                implementation(libs.logback.classic)
                implementation(libs.coroutines.debug)
            }
        }
    }
}
