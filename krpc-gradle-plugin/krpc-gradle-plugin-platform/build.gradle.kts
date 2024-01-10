/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

description = "kRPC Platform Plugin"

dependencies {
    implementation(project(":krpc-gradle-plugin-api"))
}

gradlePlugin {
    plugins {
        create("krpc-platform") {
            id = "org.jetbrains.krpc.platform"

            displayName = "kRPC Platform Plugin"
            implementationClass = "org.jetbrains.krpc.KRPCPlatformPlugin"
            description = """
                The plugin enforces proper artifacts versions for your project, depending on your Kotlin version.
                Resulting versions of the kRPC dependencies will be 'kotlinVersion-krpcVersion', for example '1.9.10-beta-4.2', where 'beta-4.2' is the kRPC version.
            """.trimIndent()
        }
    }
}
