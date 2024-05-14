/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.rpc.platform)
    application
}

group = "kotlinx.rpc.sample"
version = "1.0.0"
application {
    mainClass.set("kotlinx.rpc.sample.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors.jvm)
    implementation(libs.ktor.server.websockets.jvm)
    implementation(libs.ktor.server.host.common.jvm)
    implementation(libs.kotlinx.rpc.runtime.server)
    implementation(libs.kotlinx.rpc.runtime.serialization.json)
    implementation(libs.kotlinx.rpc.transport.ktor.server)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlinx.rpc.runtime.client)
    testImplementation(libs.kotlinx.rpc.transport.ktor.client)
    testImplementation(libs.kotlin.test.junit)
}
