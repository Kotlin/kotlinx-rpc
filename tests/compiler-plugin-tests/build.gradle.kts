/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled

    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

repositories {
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-serialization-compiler-plugin:1.9.24")
    testRuntimeOnly("com.jetbrains.intellij.platform:util:213.7172.53") // JarUtil
    testImplementation("io.github.classgraph:classgraph:4.8.158")
    testImplementation("org.jetbrains.kotlinx:compiler-plugin")
    testImplementation("org.jetbrains.kotlinx:compiler-plugin-k2")
    testImplementation(libs.kotlin.compiler.embeddable)
    testImplementation(projects.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutines.core)
    testImplementation(libs.serialization.core)
}

tasks.test {
    useJUnitPlatform()
}
