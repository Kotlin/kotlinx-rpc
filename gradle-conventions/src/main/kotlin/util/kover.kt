/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project

@Suppress("unused")
fun Project.applyKover() {
    plugins.apply("org.jetbrains.kotlinx.kover")

    rootProject.configurations.matching { it.name == "kover" }.all {
        rootProject.dependencies.add("kover", this@applyKover)
    }
}
