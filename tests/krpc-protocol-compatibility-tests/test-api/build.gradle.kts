/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("PropertyName")
@file:OptIn(ExperimentalAbiValidation::class)

import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
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
    @OptIn(ExperimentalBuildToolsApi::class, ExperimentalKotlinGradlePluginApi::class)
    // THIS IS AUTO-GENERATED, DON'T MODIFY, BEGIN
    compilerVersion.set("2.2.0")
    coreLibrariesVersion = "2.2.0"
    // END

    explicitApi = ExplicitApiMode.Disabled

    abiValidation {
        enabled.set(false)
    }
}

tasks.test {
    useJUnitPlatform()
}
