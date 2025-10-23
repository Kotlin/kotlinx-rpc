/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("PropertyName")
@file:OptIn(ExperimentalAbiValidation::class)

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.atomicfu)
}

dependencies {
    compileOnly(libs.atomicfu)
    compileOnly(libs.coroutines.core)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled

    abiValidation {
        enabled.set(false)
    }
}

tasks.test {
    useJUnitPlatform()
}
