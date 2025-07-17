/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.protobuf)
}

dependencies {
    // for the jar dependency
    testImplementation(kotlin("test"))
    testImplementation(projects.grpc.grpcCore)
    testImplementation(projects.grpc.grpcKtorServer)

    testImplementation(libs.grpc.kotlin.stub)
    testImplementation(libs.grpc.netty)

    testImplementation(libs.ktor.server.core)
    testImplementation(libs.ktor.server.test.host)
    testRuntimeOnly(libs.logback.classic)
}

rpc {
    grpc {
        enabled = true

        val globalRootDir: String by extra

        plugin {
            locator {
                path = "$globalRootDir/protobuf-plugin/build/libs/protobuf-plugin-$version-all.jar"
            }
        }

        tasksMatching { it.isTest }.all {
            dependsOn(project(":protobuf-plugin").tasks.jar)
        }
    }
}
