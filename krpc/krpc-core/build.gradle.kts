/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.RpcStrictMode
import util.applyAtomicfuPlugin

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinx.rpc)
}

applyAtomicfuPlugin()

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.krpc.krpcSerialization.krpcSerializationCore)
                implementation(projects.krpc.krpcLogging)

                api(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}
