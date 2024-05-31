/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/**
 * Use this to add a specific dependency to a source set, depending on a Kotlin version
 */
fun KotlinSourceSet.vsDependencies(
    vsSourceSetDir: String,
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kotlin.srcDirs.find { it.name == vsSourceSetDir }?.apply {
        dependencies(configure)
    }
}
