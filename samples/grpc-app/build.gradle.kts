/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.7.0"
    id("com.google.protobuf") version "0.9.5"
}

group = "kotlinx.rpc.sample"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/krpc/grpc")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-grpc-core:0.7.0-grpc-56")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("io.grpc:grpc-netty:1.73.0")
}

rpc {
    grpc {
        enabled = true
    }
}
