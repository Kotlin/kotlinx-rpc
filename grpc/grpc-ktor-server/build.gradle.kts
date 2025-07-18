/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.proto.kxrpc
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.grpc.grpcCore)
                implementation(libs.ktor.server.core)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(projects.grpc.grpcCore)
                implementation(projects.grpc.grpcKtorServer)

                implementation(libs.grpc.kotlin.stub)
                implementation(libs.grpc.netty)

                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.test.host)

                runtimeOnly(libs.logback.classic)
            }
        }
    }
}

protoSourceSets {
    jvmProtoMain {
        proto { exclude("some.proto") }
    }
}

rpc {
    grpc {
        val globalRootDir: String by extra

        protocPlugins.kxrpc {
            local {
                javaJar("$globalRootDir/protobuf-plugin/build/libs/protobuf-plugin-$version-all.jar")
            }
        }

        project.tasks.withType<BufGenerateTask>().configureEach {
            if (name.endsWith("Test")) {
                dependsOn(":protobuf-plugin:jar")
            }
        }
    }
}
