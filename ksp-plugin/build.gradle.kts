/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
}

dependencies {
    implementation(libs.ksp.runtime.api)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

val kotlinVersion: String by extra

version = "$kotlinVersion-$version"
