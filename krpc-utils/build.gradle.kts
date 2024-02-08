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
                api(project(":krpc-runtime:krpc-runtime-api"))

                implementation(libs.serialization.core)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}
