/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalAbiValidation::class)

import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import util.configureAbiFilters
import util.enableAbiValidation
import util.targets.configureJvm

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.jvm")
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    explicitApi()

    abiValidation {
        enabled = enableAbiValidation

        configureAbiFilters()
    }
}

configureJvm(isKmp = false)
