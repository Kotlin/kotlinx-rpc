/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)

                implementation(project(":kotlinx-rpc-utils:kotlinx-rpc-utils-service-loader"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-logging"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-client"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-server"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}

