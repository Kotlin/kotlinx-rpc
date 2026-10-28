/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
}

dependencies {
    api(projects.krpc.krpcSpring.krpcSpringServer)
    api(libs.spring7.security.core)

    implementation(libs.coroutines.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutines.test)
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit5.jupiter)
    testRuntimeOnly(libs.junit5.platform.launcher)
    testImplementation(projects.krpc.krpcKtor.krpcKtorClient)
    testImplementation(projects.krpc.krpcSerialization.krpcSerializationJson)
    testImplementation(libs.spring.boot4.starter.webmvc)
    testImplementation(libs.spring.boot4.starter.websocket)
    testImplementation(libs.spring.boot4.starter.security)
    testImplementation(libs.ktor.client.cio)
}

tasks.test {
    useJUnitPlatform()
}
