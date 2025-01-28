/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.5.0-grpc-6"
    id("com.google.protobuf") version "0.9.4"
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
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-grpc-core:0.5.0-grpc-6")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("io.grpc:grpc-netty:1.69.0")
}

val buildDirPath: String = project.layout.buildDirectory.get().asFile.absolutePath

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.29.3"
    }

    plugins {
        create("kotlinx-rpc") {
            artifact = "org.jetbrains.kotlinx:kotlinx-rpc-protobuf-plugin:0.5.0-grpc-6:all@jar"
        }

        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.69.0"
        }

        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }

    generateProtoTasks {
        all().all {
            plugins {
                create("kotlinx-rpc") {
                    option("debugOutput=$buildDirPath/protobuf-plugin.log")
                    option("messageMode=interface")
                }
                create("grpc")
                create("grpckt")
            }
        }
    }
}
