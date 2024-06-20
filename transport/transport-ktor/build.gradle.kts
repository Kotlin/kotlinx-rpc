/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.runtime.runtimeServer)
                implementation(projects.runtime.runtimeSerialization.runtimeSerializationJson)

                implementation(libs.ktor.websockets)
                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(projects.transport.transportKtor.transportKtorServer)
                implementation(projects.transport.transportKtor.transportKtorClient)

                implementation(libs.kotlin.test)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.client.cio)
            }
        }
    }
}
