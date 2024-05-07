/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.rpc)
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":kotlinx-rpc-runtime"))
                api(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-server"))
                api(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-client"))

                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-serialization:kotlinx-rpc-runtime-serialization-json"))

                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.test.junit)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}
