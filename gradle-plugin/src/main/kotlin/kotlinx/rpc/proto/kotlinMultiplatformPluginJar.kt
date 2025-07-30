/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import kotlinx.rpc.LIBRARY_VERSION
import org.gradle.api.Project
import org.gradle.api.provider.Provider

/**
 * Absolute path to the `protoc-gen-kotlin-multiplatform` jar.
 *
 * Can be used to customise the java executable path:
 * ```kotlin
 * rpc.grpc.protocPlugins.kotlinMultiplatform {
 *     local {
 *         javaJar(kotlinMultiplatformProtocPluginJarPath, provider { "my-path-to-java" })
 *     }
 * }
 * ```
 */
public val Project.kotlinMultiplatformProtocPluginJarPath: Provider<String>
    get() = project.configurations.named(PROTOC_GEN_KOTLIN_MULTIPLATFORM_JAR_CONFIGURATION)
        .map { it.singleFile.absolutePath }

internal fun Project.configureKotlinMultiplatformPluginJarConfiguration() {
    configurations.create(PROTOC_GEN_KOTLIN_MULTIPLATFORM_JAR_CONFIGURATION)

    dependencies.add(
        PROTOC_GEN_KOTLIN_MULTIPLATFORM_JAR_CONFIGURATION,
        mapOf(
            "group" to "org.jetbrains.kotlinx",
            "name" to "protoc-gen-kotlin-multiplatform",
            "version" to LIBRARY_VERSION,
            "classifier" to "all",
            "ext" to "jar",
        ),
    )
}
