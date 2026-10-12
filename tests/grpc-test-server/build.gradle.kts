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

    testImplementation(libs.kotlin.test.junit5)
}

tasks.test {
    useJUnitPlatform()
}

// The reference server uses plain grpc-java, so the upstream Java generators generate the code for
// the shared test protos, not the kotlinx-rpc generators. The plugin versions must match `protobuf`
// and `grpc` in libs.versions.toml. protoc 35.x generates code for protobuf-java 4.35.x.
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
        include("kxrpc/testing/client_control.proto")
        include("grpc/testing/*.proto")

        includeDefaultProtobufPlugin = false
        includeDefaultGrpcPlugin = false
        // The Java generators reject the kotlinx-rpc options (for example `explicitApiModeEnabled`), so clear them.
        plugin({ options.set(emptyMap()) }) { getByName("java") }
        plugin({ options.set(emptyMap()) }) { getByName("grpc-java") }
    }
}

// The test source set has no protos, so it doesn't use the default kotlinx-rpc generators.
sourceSets.test {
    proto {
        includeDefaultProtobufPlugin = false
        includeDefaultGrpcPlugin = false
    }
}
