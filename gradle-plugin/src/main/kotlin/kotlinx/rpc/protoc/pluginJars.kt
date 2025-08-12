/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.LIBRARY_VERSION
import org.gradle.api.Project
import org.gradle.api.provider.Provider

/**
 * Absolute path to the `protoc-gen-kotlin-multiplatform` jar.
 *
 * Can be used to customize the java executable path:
 * ```kotlin
 * rpc.protoc.plugins.kotlinMultiplatform {
 *     local {
 *         javaJar(kotlinMultiplatformProtocPluginJarPath, provider { "my-path-to-java" })
 *     }
 * }
 * ```
 */
public val Project.kotlinMultiplatformProtocPluginJarPath: Provider<String>
    get() = jarPathAccessor(PROTOC_GEN_KOTLIN_MULTIPLATFORM_JAR_CONFIGURATION)

internal fun Project.configureKotlinMultiplatformPluginJarConfiguration() {
    configureJarConfiguration(
        configurationName = PROTOC_GEN_KOTLIN_MULTIPLATFORM_JAR_CONFIGURATION,
        pluginArtifactName = "protoc-gen-kotlin-multiplatform",
    )
}

/**
 * Absolute path to the `protoc-gen-grpc-kotlin-multiplatform` jar.
 *
 * Can be used to customize the java executable path:
 * ```kotlin
 * rpc.protoc.plugins.grpcKotlinMultiplatform {
 *     local {
 *         javaJar(grpcKotlinMultiplatformProtocPluginJarPath, provider { "my-path-to-java" })
 *     }
 * }
 * ```
 */
public val Project.grpcKotlinMultiplatformProtocPluginJarPath: Provider<String>
    get() = jarPathAccessor(PROTOC_GEN_GRPC_KOTLIN_MULTIPLATFORM_JAR_CONFIGURATION)

internal fun Project.configureGrpcKotlinMultiplatformPluginJarConfiguration() {
    configureJarConfiguration(
        configurationName = PROTOC_GEN_GRPC_KOTLIN_MULTIPLATFORM_JAR_CONFIGURATION,
        pluginArtifactName = "protoc-gen-grpc-kotlin-multiplatform",
    )
}

private fun Project.jarPathAccessor(name: String): Provider<String> =
    project.configurations.named(name)
        .map { it.singleFile.absolutePath }

private fun Project.configureJarConfiguration(configurationName: String, pluginArtifactName: String) {
    configurations.create(configurationName)

    dependencies.add(
        configurationName,
        mapOf(
            "group" to "org.jetbrains.kotlinx",
            "name" to pluginArtifactName,
            "version" to LIBRARY_VERSION,
            "classifier" to "all",
            "ext" to "jar",
        ),
    )
}
