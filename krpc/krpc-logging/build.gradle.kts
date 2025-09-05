/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.logging)
                implementation(projects.utils)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.slf4j.api)
            }
        }
    }
}
