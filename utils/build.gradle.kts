/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import util.applyAtomicfuPlugin

plugins {
    alias(libs.plugins.conventions.kmp)
}

applyAtomicfuPlugin()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.serialization.core)
                implementation(libs.coroutines.core)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}
