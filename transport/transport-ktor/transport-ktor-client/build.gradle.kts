/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtime.runtimeClient)
                api(projects.runtime.runtimeSerialization)
                api(projects.transport.transportKtor)

                api(libs.ktor.client.core)
                api(libs.ktor.client.websockets)

                implementation(libs.coroutines.core)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}
