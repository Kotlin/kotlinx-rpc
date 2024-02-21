/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktor)

    alias(libs.plugins.krpc)
    alias(libs.plugins.krpc.platform)

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
    implementation(libs.ktor.server.cors.jvm)
    implementation(libs.ktor.server.websockets.jvm)

    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.logback.classic)

    implementation(libs.krpc.transport.ktor.server)
    implementation(libs.krpc.runtime.serialization.json)

    testImplementation(libs.krpc.runtime.client)
    testImplementation(libs.krpc.transport.ktor.client)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.test.host)
}