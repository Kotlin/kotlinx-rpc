/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.ksp)
    alias(libs.plugins.krpc)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)

                implementation(project(":krpc-utils:krpc-utils-service-loader"))
                implementation(project(":krpc-runtime::krpc-runtime-logging"))
                implementation(project(":krpc-runtime:krpc-runtime-client"))
                implementation(project(":krpc-runtime:krpc-runtime-server"))
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

