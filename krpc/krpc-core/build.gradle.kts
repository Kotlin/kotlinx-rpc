/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.atomicfu)
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.krpc.krpcSerialization.krpcSerializationCore)
                implementation(projects.krpc.krpcLogging)

                api(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.tests.testUtils)

                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(libs.serialization.json)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.coroutines.debug)
                implementation(libs.lincheck)
                implementation(libs.logback.classic)
            }
        }
    }
}

tasks.withType<KotlinJvmTest> {
    // lincheck agent
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}
