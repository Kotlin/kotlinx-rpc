/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalAbiValidation::class)

import compat.patrouille.configureJavaCompatibility
import compat.patrouille.configureKotlinCompatibility
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

configureJavaCompatibility(8)
configureKotlinCompatibility("2.0.0")

kotlin {
    explicitApi()

    abiValidation {
        enabled = enableAbiValidation

        configureAbiFilters()
    }
}

configureJvm(isKmp = false)
