/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("io.ktor.plugin") version "2.3.11"
    id("com.google.devtools.ksp") version "1.9.23-1.0.19"
    id("org.jetbrains.kotlinx.rpc.plugin") version "6.0-beta"
}

val kotlin_version: String by project
val logback_version: String by project

group = "kotlinx.rpc.sample"
version = "0.0.1"

application {
    mainClass.set("ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven")
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-client")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-server")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-serialization-json")

    implementation("org.jetbrains.kotlinx:kotlinx-rpc-transport-ktor-client")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-transport-ktor-server")

    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
