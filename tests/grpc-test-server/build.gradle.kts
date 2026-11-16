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

val protobufReleaseVersion = libs.versions.protobuf.get().substringAfter(".")
val grpcJavaVersion = libs.versions.grpc.asProvider().get()

rpc {
    protoc {
        plugins {
            create("java") {
                isJava = true
                remote {
                    // Using the buf.build provider as locator might hit buf's rate limit quota.
                    // If this becomes a problem we must provide the java protobuf plugin from alternative source.
                    // https://buf.build/docs/bsr/rate-limits/#filedescriptorsetservice
                    locator = "buf.build/protocolbuffers/java:v$protobufReleaseVersion"
                }
            }
            create("grpc-java") {
                isJava = true
                remote {
                    // Using the buf.build provider as source might hit buf's rate limit quota.
                    // If this becomes a problem we must provide the java grpc plugin from alternative source.
                    // https://buf.build/docs/bsr/rate-limits/#filedescriptorsetservice
                    locator = "buf.build/grpc/java:v$grpcJavaVersion"
                }
            }
        }
    }
}

sourceSets.main {
    proto {
        srcDir(project(":tests:test-grpc-protos").layout.projectDirectory.dir("src/commonMain/proto"))
        include("echo_grpc.proto")
        include("helloworld_grpc.proto")
        include("kxrpc/testing/client_control.proto")
        include("grpc/testing/*.proto")

        includeDefaultProtobufPlugin = false
        includeDefaultGrpcPlugin = false
        // The Java generators reject the kotlinx-rpc options (for example `explicitApiModeEnabled`), so clear them.
        // Remove these option overwrites once KXRPC-690 is fixed.
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
