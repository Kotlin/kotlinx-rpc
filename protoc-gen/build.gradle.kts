/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
}

group = "org.jetbrains.kotlinx"
version = rootProject.libs.versions.kotlinx.rpc.get()

dependencies {
    implementation(libs.protobuf.java)

    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)

    testImplementation(libs.kotlin.test)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "kotlinx.rpc.protobuf.MainKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier = "all"

    // Protoc plugins are all fat jars basically (the ones built on jvm)
    // be really careful of what you put in the classpath here
    from(
        configurations.runtimeClasspath.map { prop ->
            prop.map { if (it.isDirectory()) it else zipTree(it) }
        }
    )
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

tasks.test {
    useJUnitPlatform()
}

logger.lifecycle("[Protoc Plugin] kotlinx.rpc project version: $version")
