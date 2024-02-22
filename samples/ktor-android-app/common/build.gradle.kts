/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktor)
    alias(libs.plugins.krpc.platform)
}

dependencies {
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.websockets.jvm)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.krpc.transport.ktor.server)
    implementation(libs.krpc.runtime.serialization.json)
}