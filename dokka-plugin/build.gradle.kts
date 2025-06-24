/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.gradle.doctor)
    id("build-util")
    alias(libs.plugins.kotlin.jvm)
}

val rpcVersion: String = libs.versions.kotlinx.rpc.get()
val kotlinLangVersion = libs.versions.kotlin.lang.get()

group = "org.jetbrains.kotlinx"
version = rpcVersion

println("[Dokka Plugin] kotlinx.rpc project version: $version, Kotlin version: $kotlinLangVersion")

kotlin {
    jvmToolchain(8)
}

dependencies {
    compileOnly(libs.dokka.core)
    compileOnly(libs.dokka.base)

    testImplementation(kotlin("test"))
    testImplementation(libs.dokka.base)
    testImplementation("org.jetbrains.dokka:dokka-test-api:${libs.versions.dokka.get()}")
    testImplementation("org.jetbrains.dokka:dokka-base-test-utils:${libs.versions.dokka.get()}")
    testImplementation("org.jetbrains.dokka:analysis-kotlin-symbols:${libs.versions.dokka.get()}")
}
