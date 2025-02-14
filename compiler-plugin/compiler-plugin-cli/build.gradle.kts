/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import util.otherwise
import util.whenForIde

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.compiler.specific.module)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    whenForIde {
        compileOnly(libs.kotlin.compiler)
    } otherwise {
        compileOnly(libs.kotlin.compiler.embeddable)
    }

    implementation(projects.compilerPluginK2)
    implementation(projects.compilerPluginCommon)
    implementation(projects.compilerPluginBackend)
}
