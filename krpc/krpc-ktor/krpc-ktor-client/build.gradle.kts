/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.krpc.krpcClient)
                api(projects.krpc.krpcKtor.krpcKtorCore)

                api(libs.ktor.client.core)
                api(libs.ktor.client.websockets)

                implementation(libs.coroutines.core)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}
