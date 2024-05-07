/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-server"))
                api(project(":kotlinx-rpc-transport:kotlinx-rpc-transport-ktor"))
                api(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-serialization"))

                api(libs.ktor.server.core)
                api(libs.ktor.server.websockets)

                implementation(libs.coroutines.core)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}
