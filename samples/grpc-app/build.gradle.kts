/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.11.0-grpc-185"
}

group = "kotlinx.rpc.sample"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://redirector.kotlinlang.org/maven/kxrpc-grpc")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-protobuf-core:0.11.0-grpc-185")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-grpc-client:0.11.0-grpc-185")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-grpc-server:0.11.0-grpc-185")
    implementation("ch.qos.logback:logback-classic:1.5.20")
    implementation("io.grpc:grpc-netty:1.79.0")
}

rpc {
    protoc()
}
