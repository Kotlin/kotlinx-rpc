/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
}


dependencies {
    api(projects.krpc.krpcServer)

    api(libs.spring7.context)
    api(libs.spring7.websocket)

    implementation(libs.coroutines.core)
    implementation(libs.kotlin.logging)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.spring7.core.test)
    testImplementation(project(":krpc:krpc-ktor:krpc-ktor-client"))
}