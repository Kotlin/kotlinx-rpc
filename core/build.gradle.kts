/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.utils)
                implementation(projects.krpc.krpcSerialization.krpcSerializationCore)

                api(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)

                implementation(projects.krpc.krpcLogging)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.kotlin.js.wrappers)
            }
        }
    }
}
