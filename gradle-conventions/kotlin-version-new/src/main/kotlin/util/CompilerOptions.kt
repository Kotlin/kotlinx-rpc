/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

/**
 * Set the compatibility mode to the lower supported language version.
 *
 * This should be lined up with the minimal supported compiler plugin version
 */
val projectLanguageVersion: KotlinCommonCompilerOptions.() -> Unit = {
    languageVersion.set(KotlinVersion.KOTLIN_1_7)
    apiVersion.set(KotlinVersion.KOTLIN_1_7)
}
