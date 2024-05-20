/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

description = "kotlinx.rpc Platform Plugin"

dependencies {
    implementation(project(":kotlinx-rpc-gradle-plugin-api"))
}

gradlePlugin {
    plugins {
        create("kotlinxRpcPlatform") {
            id = "org.jetbrains.kotlinx.rpc.platform"

            displayName = "kotlinx.rpc Platform Plugin"
            implementationClass = "kotlinx.rpc.RPCPlatformPlugin"
            description = """
                The plugin enforces proper artifacts versions for your project, depending on your Kotlin version.
                Resulting versions of the kotlinx.rpc dependencies will be 'kotlinVersion-kotlinxRpcVersion', for example '1.9.24-0.1.0', where '0.1.0' is the kotlinx.rpc version.
            """.trimIndent()
        }
    }
}
