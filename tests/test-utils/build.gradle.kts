/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
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
                implementation(libs.coroutines.core)
                implementation(libs.coroutines.test)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.coroutines.debug)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}
