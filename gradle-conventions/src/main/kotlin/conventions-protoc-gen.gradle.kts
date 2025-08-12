/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.other.libs
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("conventions-jvm")
}

dependencies {
    implementation(libs.protobuf.java)

    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)

    testImplementation(libs.kotlin.test)
}

tasks.jar {
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
