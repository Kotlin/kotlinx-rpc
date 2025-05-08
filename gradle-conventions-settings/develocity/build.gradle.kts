/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.gradle.kotlin.dsl)
}

dependencies {
    implementation("com.gradle:develocity-gradle-plugin:3.17")
    implementation("com.gradle:common-custom-user-data-gradle-plugin:2.2.1")
}
