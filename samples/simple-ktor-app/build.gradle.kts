/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    id("com.google.devtools.ksp") version "1.9.22-1.0.16"
    id("org.jetbrains.krpc.plugin") version "5.2-beta"
}

val kotlin_version: String by project
val logback_version: String by project

group = "com.example"
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
    implementation("org.jetbrains.krpc:krpc-runtime-client")
    implementation("org.jetbrains.krpc:krpc-runtime-server")
    implementation("org.jetbrains.krpc:krpc-runtime-serialization-json")

    implementation("org.jetbrains.krpc:krpc-transport-ktor-client")
    implementation("org.jetbrains.krpc:krpc-transport-ktor-server")

    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
