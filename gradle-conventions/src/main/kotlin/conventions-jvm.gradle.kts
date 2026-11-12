/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalAbiValidation::class)

import compat.patrouille.configureJavaCompatibility
import compat.patrouille.configureKotlinCompatibility
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jetbrains.kotlinx.library.api.watchdog.WatchdogGradleExtension
import util.configureAbiFilters
import util.enableAbiValidation
import util.other.isPublicModule
import util.other.optionalProperty
import util.targets.configureJvm

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.jvm")
}

java {
    withSourcesJar()
}

if (project.name != "gradle-plugin") {
    configureJavaCompatibility(8)

    tasks.named<JavaCompile>("compileTestJava") {
        options.release = 17
    }

    tasks.named<KotlinJvmCompile>("compileTestKotlin") {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            // compat-patrouille adds `-Xjdk-release=8` to the options of all compilations.
            freeCompilerArgs = kotlin.compilerOptions.freeCompilerArgs.map { args ->
                args.map { if (it.startsWith("-Xjdk-release=")) "-Xjdk-release=17" else it }
            }
        }
    }
}

configureKotlinCompatibility("2.3.0")

// The watchdog plugin adds its annotations artifact to every compilation, even when `enabled` is false.
// The artifact targets the Kotlin version of the plugin, so an older `compilerVersion` can't read it.
// A module that pins an older compiler sets `kotlinx.rpc.apiWatchdog.skip=true` in its `gradle.properties`.
val apiWatchdogSkip by optionalProperty("apiWatchdog")

if (!apiWatchdogSkip) {
    apply(plugin = "org.jetbrains.kotlin.library.api-watchdog")

    configure<WatchdogGradleExtension> {
        enabled = isPublicModule
        suggestAbiValidation = enableAbiValidation
        javaInterop.enabled = false
    }
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
