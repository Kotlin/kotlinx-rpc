/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import util.enableContextParameters
import util.whenForIde
import util.whenKotlinCompilerIsAtLeast

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.compiler.specific.module)
}

tasks.jar {
    // important for IDEA plugin
    whenForIde {
        archiveClassifier.set("for-ide")
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled

    rootProject.whenKotlinCompilerIsAtLeast(2, 2, 0) {
        enableContextParameters()
    }
}

dependencies {
    compileOnly(libs.kotlin.compiler)

    implementation(projects.compilerPluginCommon)
}
