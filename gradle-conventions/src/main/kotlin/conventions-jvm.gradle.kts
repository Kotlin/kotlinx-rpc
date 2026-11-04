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

if (project.name != "gradle-plugin") {
    // Modules whose dependencies require a newer JVM (e.g., Spring 7 requires 17)
    // can override the default via `kotlinx.rpc.jvm.target` in their gradle.properties
    val jvmTarget = (project.findProperty("kotlinx.rpc.jvm.target") as String?)?.toInt() ?: 8
    configureJavaCompatibility(jvmTarget)
}

configureKotlinCompatibility("2.2.0")

kotlin {
    explicitApi()

    if (enableAbiValidation) {
        abiValidation {
            configureAbiFilters()
        }
    }
}

configureJvm(isKmp = false)
