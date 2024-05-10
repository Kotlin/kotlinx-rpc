/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.rpc.platform)

    application
    distribution
}

application {
    mainClass.set("ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":common"))

    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.websockets.jvm)

    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.logback.classic)

    implementation(libs.kotlinx.rpc.transport.ktor.server)
    implementation(libs.kotlinx.rpc.runtime.serialization.json)

    testImplementation(libs.kotlinx.rpc.runtime.client)
    testImplementation(libs.kotlinx.rpc.transport.ktor.client)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.test.host)
}