/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.atomicfu)
}

val main: SourceSet by sourceSets.getting
val test: SourceSet by sourceSets.getting

val oldApi: SourceSet by sourceSets.creating {
    compileClasspath += main.output
    compileClasspath += test.compileClasspath
    runtimeClasspath += main.output
    runtimeClasspath += test.runtimeClasspath
}

val newApi: SourceSet by sourceSets.creating {
    compileClasspath += main.output
    compileClasspath += test.compileClasspath
    runtimeClasspath += main.output
    runtimeClasspath += test.runtimeClasspath
}

val compatibilityTestSourcesDir: File = project.layout.buildDirectory.dir("compatibilityTestSources").get().asFile

val copyOldToTestResources by tasks.register<Copy>("copyOldToTestResources") {
    dependsOn(oldApi.output)
    from(oldApi.output)
    into(compatibilityTestSourcesDir.resolve("old"))
}

val copyNewToTestResources by tasks.register<Copy>("copyNewToTestResources") {
    dependsOn(newApi.output)
    from(newApi.output)
    into(compatibilityTestSourcesDir.resolve("new"))
}

test.resources {
    srcDir(compatibilityTestSourcesDir)
}

tasks.processTestResources.configure {
    dependsOn(copyOldToTestResources, copyNewToTestResources)
}

dependencies {
    api(libs.atomicfu)

    api(projects.krpc.krpcCore)
    api(projects.krpc.krpcServer)
    api(projects.krpc.krpcClient)

    implementation(projects.krpc.krpcSerialization.krpcSerializationJson)

    implementation(libs.serialization.core)
    implementation(libs.coroutines.test)
    implementation(libs.kotlin.test.junit5)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.slf4j.api)
    testImplementation(libs.logback.classic)
    testImplementation(libs.coroutines.debug)

    testImplementation(projects.tests.testUtils)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

tasks.test {
    useJUnitPlatform()
}
