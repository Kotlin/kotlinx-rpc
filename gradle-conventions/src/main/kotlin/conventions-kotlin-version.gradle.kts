/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import util.other.libs
import util.withKotlinJvmExtension
import util.withKotlinKmpExtension

fun KotlinProjectExtension.optInForRpcApi() {
    sourceSets.all {
        languageSettings.optIn("kotlinx.rpc.internal.utils.InternalRpcApi")
        languageSettings.optIn("kotlinx.rpc.internal.utils.ExperimentalRpcApi")
    }
}

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
    optInForRpcApi()
}

withKotlinKmpExtension {
    optInForRpcApi()

    // We don't use `compat-patrouille` here because it sets stdlib version, and that breaks WASM tests.
    compilerOptions.setProjectLanguageVersion()
}
