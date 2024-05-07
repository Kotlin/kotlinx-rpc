/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.atomicfu)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":kotlinx-rpc-runtime"))
                implementation(project(":kotlinx-rpc-utils"))
                implementation(project(":kotlinx-rpc-utils:kotlinx-rpc-utils-service-loader"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-serialization"))

                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)

                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-logging"))
            }
        }
    }
}
