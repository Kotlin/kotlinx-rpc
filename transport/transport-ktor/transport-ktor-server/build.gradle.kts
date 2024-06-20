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
                api(projects.runtime.runtimeServer)
                api(projects.runtime.runtimeSerialization)
                api(projects.transport.transportKtor)

                api(libs.ktor.server.core)
                api(libs.ktor.server.websockets)

                implementation(libs.coroutines.core)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}
