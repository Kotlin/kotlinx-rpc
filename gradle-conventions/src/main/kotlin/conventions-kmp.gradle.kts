/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalAbiValidation::class)

import compat.patrouille.configureJavaCompatibility
import compat.patrouille.configureKotlinCompatibility
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import util.*
import util.targets.configureJs
import util.targets.configureJvm
import util.targets.configureKotlinExtension
import util.targets.configureWasm
import util.targets.withKmpConfig

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    explicitApi()

    abiValidation {
        enabled = enableAbiValidation

        klib {
            enabled = enableAbiValidation
        }

        configureAbiFilters()
    }
}

withKmpConfig {
    configureKotlinExtension()
    configureJs()
    configureWasm()
}

configureJvm(isKmp = true)

configureJavaCompatibility(8)
configureKotlinCompatibility("2.0.0")
