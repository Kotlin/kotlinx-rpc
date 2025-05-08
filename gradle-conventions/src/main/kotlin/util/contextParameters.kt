/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

fun Project.enableContextParameters() {
    withKotlinJvmExtension {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }

    withKotlinKmpExtension {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }
}
