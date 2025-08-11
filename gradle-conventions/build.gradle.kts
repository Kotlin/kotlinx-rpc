/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.gradle.publish.gradle.plugin)
    implementation(libs.compat.patrouille.gradle.plugin)

    implementation(libs.kover.gradle.plugin)

    // https://stackoverflow.com/questions/76713758/use-version-catalog-inside-precompiled-gradle-plugin
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
