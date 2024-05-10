/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

description = "kotlinx.rpc Gradle Plugin"

dependencies {
    implementation(project(":kotlinx-rpc-gradle-plugin-api"))
    implementation(project(":kotlinx-rpc-gradle-plugin-platform"))

    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("kotlinx-rpc-all") {
            id = "org.jetbrains.kotlinx.rpc.plugin"

            displayName = "kotlinx.rpc Gradle Plugin"
            implementationClass = "kotlinx.rpc.RPCGradlePlugin"
            description = """
                The plugin ensures correct configurations for your project, that will allow proper code generation. 
                Additionally, it enforces proper artifacts versions for your project, depending on your Kotlin version. (via "org.jetbrains.kotlinx.rpc.platform" plugin)
            """.trimIndent()
        }
    }
}
