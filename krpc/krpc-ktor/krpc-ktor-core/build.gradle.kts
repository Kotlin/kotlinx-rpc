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

                // KRPC-137 Remove temporary explicit dependencies in 2.1.10 and unmute compiler tests
                implementation(projects.core)
                implementation(projects.utils)

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

                implementation(libs.kotlin.test)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.test.host)
            }
        }
    }
}
