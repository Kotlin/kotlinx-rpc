/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.protoc.proto

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
    application
}

application {
    mainClass = "kotlinx.rpc.grpc.test.server.TestServerKt"
}

dependencies {
    implementation(libs.grpc.netty)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.protobuf.java)
}

// The reference server is plain grpc-java, so the shared test protos are compiled with the
// upstream Java generators instead of kotlinx-rpc's. Keep the plugin versions in sync with
// `protobuf` (protoc 35.x generates code for protobuf-java 4.35.x) and `grpc` in libs.versions.toml.
rpc {
    protoc {
        plugins {
            create("java") {
                isJava = true
                remote {
                    locator = "buf.build/protocolbuffers/java:v35.1"
                }
            }
            create("grpc-java") {
                isJava = true
                remote {
                    locator = "buf.build/grpc/java:v1.81.0"
                }
            }
        }
    }
}

sourceSets.main {
    proto {
        srcDir(project(":tests:test-protos").layout.projectDirectory.dir("src/commonMain/proto"))
        include("echo_grpc.proto")
        include("helloworld_grpc.proto")
        include("grpc/testing/*.proto")

        includeDefaultProtobufPlugin = false
        includeDefaultGrpcPlugin = false
        // Clear the kotlinx-rpc options (e.g. `explicitApiModeEnabled`) that the Java generators reject.
        plugin({ options.set(emptyMap()) }) { getByName("java") }
        plugin({ options.set(emptyMap()) }) { getByName("grpc-java") }
    }
}

// There are no test protos; skip the default kotlinx-rpc generators for the test source set.
sourceSets.test {
    proto {
        includeDefaultProtobufPlugin = false
        includeDefaultGrpcPlugin = false
    }
}
