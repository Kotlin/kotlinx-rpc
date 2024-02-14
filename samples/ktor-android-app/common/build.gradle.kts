/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    id("io.ktor.plugin") version "2.3.8"
    id("org.jetbrains.krpc.platform") version "5.3-beta"
    id("org.jetbrains.krpc.plugin") version "5.3-beta"
}

dependencies {
    implementation("io.ktor:ktor-server-cio-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")

    implementation("org.jetbrains.krpc:krpc-transport-ktor-server")
    implementation("org.jetbrains.krpc:krpc-runtime-serialization-json")
}