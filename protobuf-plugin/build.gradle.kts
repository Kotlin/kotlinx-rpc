/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.serialization)
    id("com.google.protobuf")
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.24.1")

    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)

    testImplementation(projects.grpc.grpcCore)
    testImplementation(libs.coroutines.core)
    testImplementation(libs.kotlin.test)
    testImplementation("io.grpc:grpc-stub:1.68.2")
    testImplementation("io.grpc:grpc-netty:1.68.2")
    testImplementation("io.grpc:grpc-protobuf:1.68.2")
    testImplementation("io.grpc:grpc-kotlin-stub:1.4.1")
    testImplementation("com.google.protobuf:protobuf-java-util:4.28.2")
    testImplementation("com.google.protobuf:protobuf-kotlin:4.28.2")
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
        artifact = "com.google.protobuf:protoc:3.24.1"
    }

    plugins {
        create("kotlinx-rpc") {
            path = "$buildDirPath/libs/protobuf-plugin-$version.jar"
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
