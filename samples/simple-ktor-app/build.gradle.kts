/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("io.ktor.plugin") version "3.1.2"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.6.2"
}

group = "kotlinx.rpc.sample"
version = "0.0.1"

application {
    mainClass.set("ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:0.6.2")

    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server:0.6.2")

    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.1.20")
}
