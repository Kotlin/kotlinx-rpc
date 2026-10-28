/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.serialization)
}

dependencies {
    api(projects.krpc.krpcSpring.krpcSpringServer)
    api(libs.spring.boot4.autoconfigure)
    api(libs.spring.boot4.starter.websocket)

    compileOnly(projects.krpc.krpcSerialization.krpcSerializationJson)
    compileOnly(projects.krpc.krpcSerialization.krpcSerializationCbor)
    compileOnly(projects.krpc.krpcSerialization.krpcSerializationProtobuf)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutines.test)
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit5.jupiter)
    testRuntimeOnly(libs.junit5.platform.launcher)
    testImplementation(libs.spring7.core.test)
    testImplementation(libs.spring.boot4.starter.test)
    testImplementation(libs.spring.boot4.starter.webmvc)
    testImplementation(projects.krpc.krpcKtor.krpcKtorClient)
    testImplementation(projects.krpc.krpcSerialization.krpcSerializationJson)
    testImplementation(projects.krpc.krpcSerialization.krpcSerializationCbor)
    testImplementation(projects.krpc.krpcSerialization.krpcSerializationProtobuf)
    testImplementation(libs.serialization.json)
    testImplementation(libs.ktor.client.cio)
}

tasks.test {
    useJUnitPlatform()
}