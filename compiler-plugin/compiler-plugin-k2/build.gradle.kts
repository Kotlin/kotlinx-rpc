/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.compiler.specific.module)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
    compileOnly("org.jetbrains.kotlin:kotlin-serialization-compiler-plugin:1.9.24")
    implementation(projects.compilerPluginCommon)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }
}
