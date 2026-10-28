/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalAbiValidation::class)

import compat.patrouille.configureJavaCompatibility
import compat.patrouille.configureKotlinCompatibility
import gradle.kotlin.dsl.accessors._55a6c9f42184da0c402717fd5db9f764.apiWatchdog
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import util.configureAbiFilters
import util.enableAbiValidation
import util.targets.configureJvm

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.library.api-watchdog")
}

java {
    withSourcesJar()
}

if (project.name != "gradle-plugin") {
    configureJavaCompatibility(8)
}

configureKotlinCompatibility("2.2.0")

val isPublicModule: Boolean by extra(false)

apiWatchdog {
    enabled = isPublicModule
    suggestAbiValidation = enableAbiValidation
}

kotlin {
    explicitApi()

    if (enableAbiValidation) {
        abiValidation {
            configureAbiFilters()
        }
    }
}

configureJvm(isKmp = false)
