/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.compiler.specific.module)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled

    sourceSets.all {
        languageSettings.optIn("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
    }
}

dependencies {
    compileOnly(libs.kotlin.compiler)

    implementation(projects.compilerPluginCommon)
}
