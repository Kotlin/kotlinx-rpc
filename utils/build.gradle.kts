/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.atomicfu)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-api"))

                implementation(libs.serialization.core)
                implementation(libs.coroutines.core)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}
