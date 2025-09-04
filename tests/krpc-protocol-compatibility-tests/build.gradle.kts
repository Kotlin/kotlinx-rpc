/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.atomicfu)
}

val main: SourceSet by sourceSets.getting
val test: SourceSet by sourceSets.getting

val compatibilityTestSourcesDir: File = project.layout.buildDirectory.dir("compatibilityTestSources").get().asFile

fun versioned(name: String): Configuration {
    val configuration = configurations.create(name) {
        isCanBeConsumed = true
        isCanBeResolved = true
        isTransitive = true
    }

    val sourceSet = sourceSets.create(name) {
        compileClasspath += main.output
        runtimeClasspath += main.output

        compileClasspath += configuration
        runtimeClasspath += configuration
    }

    val copySourceSetTestResources by tasks.register<Copy>("copy_${name}_ToTestResources") {
        dependsOn(sourceSet.output)
        from(sourceSet.output)
        into(compatibilityTestSourcesDir.resolve(name))
    }

    tasks.processTestResources.configure {
        dependsOn(copySourceSetTestResources)
    }

    return configuration
}

val v0_9 = versioned("v0_9")
val v0_8 = versioned("v0_8")

test.resources {
    srcDir(compatibilityTestSourcesDir)
}

fun DependencyHandlerScope.versioned(configuration: Configuration, version: String) {
    add(configuration.name, "org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:$version")
    add(configuration.name, "org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:$version")
    add(configuration.name, "org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:$version")
    add(configuration.name, libs.atomicfu)
}

dependencies {
    api(libs.atomicfu)
    implementation(libs.serialization.core)
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.reflect)

    versioned(v0_9, "0.9.1")
    versioned(v0_8, "0.8.1")

    // current version is in test source set
    testImplementation(projects.krpc.krpcCore)
    testImplementation(projects.krpc.krpcServer)
    testImplementation(projects.krpc.krpcClient)
    testImplementation(projects.krpc.krpcSerialization.krpcSerializationJson)

    testImplementation(libs.coroutines.test)
    testImplementation(libs.kotlin.test.junit5)

    testImplementation(libs.slf4j.api)
    testImplementation(libs.logback.classic)
    testImplementation(libs.coroutines.debug)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

tasks.test {
    useJUnitPlatform()
}
