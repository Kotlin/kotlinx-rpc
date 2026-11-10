/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.ignoreDokkaWarnings

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

// Private `KrpcClient` members have types from `implementation` dependencies of krpc-client.
// Dokka 2.3.0 reports these types as unresolved.
ignoreDokkaWarnings(
    """`(kotlinx\.atomicfu\.AtomicLong|kotlinx\.rpc\.krpc\.internal\.logging\.RpcInternalCommonLogger)` """ +
        """is unresolved in kotlinx\.rpc\.krpc\.client/KrpcClient/.*""",
)
