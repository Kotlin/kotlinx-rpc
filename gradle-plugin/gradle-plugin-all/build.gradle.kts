/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

description = "kotlinx.rpc Gradle Plugin"

dependencies {
    implementation(projects.gradlePluginApi)
    implementation(projects.gradlePluginPlatform)

    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("plugin") {
            id = "org.jetbrains.kotlinx.rpc.plugin"

            displayName = "kotlinx.rpc Gradle Plugin"
            implementationClass = "kotlinx.rpc.RPCGradlePlugin"
            description = """
                The plugin ensures correct RPC configurations for your project, that will allow proper code generation. 
                
                Additionally, it enforces proper artifacts versions for your project, depending on your Kotlin version.
                Resulting versions of the kotlinx.rpc dependencies will be 'kotlinVersion-kotlinxRpcVersion', for example '1.9.24-0.1.0', where '0.1.0' is the kotlinx.rpc version.
            """.trimIndent()
        }
    }
}
