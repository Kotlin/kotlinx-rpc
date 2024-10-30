/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import util.lowercase
import util.withKotlinJvmExtension
import util.withKotlinKmpExtension

/**
 * Set the compatibility mode to the lower supported language version.
 *
 * This should be lined up with the minimal supported compiler plugin version.
 *
 * We update the language version only for the 'main' sources sets.
 * This makes our tests execute against the latest compiler plugin version (for example, with K2 instead of K1).
 */
fun KotlinCommonCompilerOptions.setProjectLanguageVersion() {
    languageVersion.set(KotlinVersion.KOTLIN_2_0)
    apiVersion.set(KotlinVersion.KOTLIN_2_0)
}

withKotlinJvmExtension {
    target.compilations
        .filter { it.isMain }
        .forEach { compilation ->
            compilation.compileJavaTaskProvider.configure {
                compilerOptions.setProjectLanguageVersion()
            }
        }
}

withKotlinKmpExtension {
    targets.flatMap { it.compilations }
        .filter { it.isMain }
        .forEach { compilation ->
            compilation.compileTaskProvider.configure {
                compilerOptions.setProjectLanguageVersion()
            }
        }
}

val KotlinCompilation<*>.isMain get() = name.lowercase().contains("main")
