/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
    id("com.google.protobuf")
}

dependencies {
    implementation(projects.utils)

    implementation("com.google.protobuf:protobuf-java:3.24.1")

    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)


    testImplementation(libs.coroutines.core)
    testImplementation("io.grpc:grpc-stub:1.57.2")
    testImplementation("io.grpc:grpc-protobuf:1.57.2")
    testImplementation("com.google.protobuf:protobuf-java-util:3.24.1")
    testImplementation("io.grpc:grpc-kotlin-stub:1.3.1")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "kotlinx.rpc.protobuf.MainKt"

        // for plugin-test module to locate dependencies when running protoc plugin
        attributes["Class-Path"] = configurations.runtimeClasspath.get()
            .joinToString(" ") { it.absolutePath }
    }
}

val buildDir: String = project.layout.buildDirectory.get().asFile.absolutePath

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.1"
    }

    plugins {
        create("kotlinx-rpc") {
            path = "$buildDir/libs/protobuf-plugin-$version.jar"
        }

        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.57.2"
        }

        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.1:jdk8@jar"
        }
    }

    generateProtoTasks {
        all().matching { it.isTest }.all {
            plugins {
                create("kotlinx-rpc") {
                    option("debugOutput=$buildDir/protobuf-plugin.log")
                }
                create("grpc")
                create("grpckt")
            }

            dependsOn(tasks.jar)
        }
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}
