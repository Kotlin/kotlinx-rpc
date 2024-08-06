/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.defaultConventionConfiguration

plugins {
    alias(libs.plugins.gradle.kotlin.dsl)
}

defaultConventionConfiguration()

dependencies {
    implementation(":gradle-conventions-settings")
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
}
