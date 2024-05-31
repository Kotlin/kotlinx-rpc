/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)

                implementation(projects.krpc.krpcLogging)
                implementation(projects.krpc.krpcClient)
                implementation(projects.krpc.krpcServer)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.slf4j.api)
                implementation(libs.logback.classic)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}

