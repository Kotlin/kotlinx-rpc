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
                api(project(":krpc-runtime"))

                implementation(project(":krpc-utils"))
                implementation(project(":krpc-utils:krpc-utils-service-loader"))
                implementation(project(":krpc-runtime:krpc-runtime-serialization"))
                implementation(project(":krpc-runtime::krpc-runtime-logging"))

                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}
