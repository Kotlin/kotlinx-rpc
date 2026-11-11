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

                // `KtorRpcClient` extends `KrpcClient`, so Dokka resolves the private members of `KrpcClient` too.
                // The types of `KrpcClient.logger` and `KrpcClient.callCounter` come from these libraries.
                // krpc-client doesn't expose them, so Dokka can't find the types in `commonMain` without these lines.
                compileOnly(projects.krpc.krpcLogging)
                compileOnly(libs.atomicfu)
            }
        }
    }
}
