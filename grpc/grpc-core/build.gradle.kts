/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.proto.kotlinMultiplatform
import org.gradle.kotlin.dsl.withType

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.utils)
                api(libs.coroutines.core)

                implementation(libs.atomicfu)
            }
        }

        jvmMain {
            dependencies {
                api(libs.grpc.api)
                api(libs.grpc.util)
                api(libs.grpc.stub)
                api(libs.grpc.protobuf)
                implementation(libs.grpc.kotlin.stub) // causes problems to jpms if api
                api(libs.protobuf.java.util)
                implementation(libs.protobuf.kotlin)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.grpc.grpcCore)
                implementation(libs.coroutines.core)
                implementation(libs.coroutines.test)
                implementation(libs.kotlin.test)

                implementation(libs.grpc.stub)
                implementation(libs.grpc.netty)
                implementation(libs.grpc.protobuf)
                implementation(libs.grpc.kotlin.stub)
                implementation(libs.protobuf.java.util)
                implementation(libs.protobuf.kotlin)
            }
        }
    }
}


protoSourceSets {
    jvmTest {
        proto {
            exclude("exclude/**")
        }
    }
}

rpc {
    grpc {
        val globalRootDir: String by extra

        protocPlugins.kotlinMultiplatform {
            local {
                javaJar("$globalRootDir/protoc-gen/build/libs/protoc-gen-$version-all.jar")
            }
        }

        project.tasks.withType<BufGenerateTask>().configureEach {
            if (name.endsWith("Test")) {
                dependsOn(gradle.includedBuild("protoc-gen").task(":jar"))
            }
        }
    }
}
