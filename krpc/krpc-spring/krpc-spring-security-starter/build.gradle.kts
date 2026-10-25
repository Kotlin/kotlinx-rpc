/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
}

dependencies {
    api(projects.krpc.krpcSpring.krpcSpringSecurity)
    api(libs.spring.boot4.autoconfigure)

    // Users declare these explicitly. If either is missing, the auto-configuration backs off.
    compileOnly(projects.krpc.krpcSpring.krpcSpringServerStarter)
    compileOnly(libs.spring.boot4.starter.security)

    testImplementation(projects.krpc.krpcSpring.krpcSpringServerStarter)
    testImplementation(libs.spring.boot4.starter.security)

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
    testImplementation(libs.serialization.json)
    testImplementation(libs.ktor.client.cio)
}

tasks.test {
    useJUnitPlatform()
}
