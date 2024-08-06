/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import util.optionalProperty
import util.projectLanguageVersion

val useK2Plugin: Boolean by optionalProperty()

@OptIn(ExperimentalKotlinGradlePluginApi::class)
configure<KotlinMultiplatformExtension> {
    compilerOptions(projectLanguageVersion(useK2Plugin))
}
