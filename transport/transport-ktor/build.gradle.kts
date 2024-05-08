/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.ksp)
    alias(libs.plugins.rpc)
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-server"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-serialization:kotlinx-rpc-runtime-serialization-json"))

                implementation(libs.ktor.websockets)
                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(project(":kotlinx-rpc-transport:kotlinx-rpc-transport-ktor:kotlinx-rpc-transport-ktor-server"))
                implementation(project(":kotlinx-rpc-transport:kotlinx-rpc-transport-ktor:kotlinx-rpc-transport-ktor-client"))

                implementation(libs.kotlin.test)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.client.cio)
            }
        }
    }
}
