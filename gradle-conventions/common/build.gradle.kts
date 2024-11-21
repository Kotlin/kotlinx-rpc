/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.defaultConventionConfiguration

plugins {
    alias(libs.plugins.gradle.kotlin.dsl)
}

defaultConventionConfiguration()

val isLatestKotlinVersion: Boolean by extra

dependencies {
    implementation(":gradle-conventions-settings")

    api(libs.kotlin.gradle.plugin)
    api(libs.detekt.gradle.plugin)
    api(libs.binary.compatibility.validator.gradle.plugin)

    if (isLatestKotlinVersion) {
        api(libs.kover.gradle.plugin)
    }

    // https://stackoverflow.com/questions/76713758/use-version-catalog-inside-precompiled-gradle-plugin
    api(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
