/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.rpc)
}

dependencies {
    implementation(projects.krpc.krpcClient)
    implementation(projects.krpc.krpcServer)

    implementation(projects.tests.codegenTests.codegenTestsMpp)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.coroutines.core)
    implementation(libs.serialization.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.slf4j.api)
    testImplementation(libs.logback.classic)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}
