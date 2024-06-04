/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    `java-platform`
    `maven-publish`
    alias(libs.plugins.conventions.common)
}

// skip csm modules, as their versions are prefixed with a Kotlin version
val csm = setOf(
    "kotlinx-rpc-compiler-plugin",
    "kotlinx-rpc-compiler-plugin-core",
    "kotlinx-rpc-compiler-plugin-1_7",
    "kotlinx-rpc-compiler-plugin-1_7_2",
    "kotlinx-rpc-compiler-plugin-1_8",
    "kotlinx-rpc-compiler-plugin-1_9",
    "kotlinx-rpc-ksp-plugin",
)

dependencies {
    constraints {
        rootProject.subprojects.filter {
            it.name.startsWith(KOTLINX_RPC_PREFIX) && it != project && it.name !in csm
        }.forEach { project ->
            api(project)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("kotlinxRpcPlatform") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["javaPlatform"])
        }
    }
}
