/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.serialization)
    alias(libs.plugins.protobuf)
}

dependencies {
    implementation(libs.protobuf.java)

    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)

    testImplementation(projects.grpc.grpcCore)
    testImplementation(libs.coroutines.core)
    testImplementation(libs.kotlin.test)

    testImplementation(libs.grpc.stub)
    testImplementation(libs.grpc.netty)
    testImplementation(libs.grpc.protobuf)
    testImplementation(libs.grpc.kotlin.stub)
    testImplementation(libs.protobuf.java.util)
    testImplementation(libs.protobuf.kotlin)
}

sourceSets {
    test {
        proto {
            exclude(
                "**/empty_deprecated.proto",
                "**/enum.proto",
                "**/example.proto",
                "**/funny_types.proto",
                "**/map.proto",
                "**/multiple_files.proto",
                "**/nested.proto",
                "**/one_of.proto",
                "**/options.proto",
                "**/with_comments.proto",
            )
        }
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "kotlinx.rpc.protobuf.MainKt"

    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Protoc plugins are all fat jars basically (the ones built on jvm)
    // be really careful of what you put in the classpath here
    from(
        configurations.runtimeClasspath.map { prop ->
            prop.map { if (it.isDirectory()) it else zipTree(it) }
        }
    )
}

val buildDirPath: String = project.layout.buildDirectory.get().asFile.absolutePath

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }

    plugins {
        create("kotlinx-rpc") {
            path = "$buildDirPath/libs/protobuf-plugin-$version.jar"
        }

        create("grpc") {
            artifact = libs.grpc.protoc.gen.java.get().toString()
        }

        create("grpckt") {
            artifact = libs.grpc.protoc.gen.kotlin.get().toString() + ":jdk8@jar"
        }
    }

    generateProtoTasks {
        all().matching { it.isTest }.all {
            plugins {
                create("kotlinx-rpc") {
                    option("debugOutput=$buildDirPath/protobuf-plugin.log")
                    option("messageMode=interface")
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

tasks.test {
    useJUnitPlatform()
}
