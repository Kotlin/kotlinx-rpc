/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.setLanguageVersion() {
    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            freeCompilerArgs += "-language-version=1.7"
            freeCompilerArgs += "-api-version=1.7"
        }
    }
}
