/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.compiler.specific.module)
}

kotlin {
    sourceSets {
        commonMain {
            vsDependencies("latest") {
                implementation(libs.kotlin.logging)
            }

            vsDependencies("v_1_7") {
                implementation(libs.kotlin.logging.legacy)
            }
        }
    }
}
