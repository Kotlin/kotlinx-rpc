/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.gradle.doctor)
    alias(libs.plugins.kotlin.jvm)
}

group = "org.jetbrains.kotlinx"
version = libs.versions.kotlinx.rpc.get()

logger.lifecycle("[Dokka Plugin] kotlinx.rpc project version: $version, Kotlin version: ${libs.versions.kotlin.lang.get()}")

kotlin {
    jvmToolchain(8)
}

dependencies {
    compileOnly(libs.dokka.core)
    compileOnly(libs.dokka.base)

    testImplementation(kotlin("test"))
    testImplementation(libs.dokka.base)
    testImplementation(libs.dokka.test.api)
    testImplementation(libs.dokka.base.test.utils)
    testImplementation(libs.dokka.analysis.kotlin.symbols)
}
