/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import kotlinx.rpc.BUF_TOOL_VERSION
import kotlinx.rpc.LIBRARY_VERSION
import org.gradle.api.Project

// https://maven.pkg.jetbrains.space/public/p/krpc/grpc/org/jetbrains/kotlinx/kotlinx-rpc-protobuf-plugin/0.8.1-grpc-99/kotlinx-rpc-protobuf-plugin-0.8.1-grpc-99-all.jar

public const val KXRPC_PLUGIN_JAR_CONFIGURATION: String = "kxrpcPluginJar"

internal fun Project.configureKxRpcPluginJarConfiguration() {
    configurations.create(KXRPC_PLUGIN_JAR_CONFIGURATION)

    dependencies.add(
        KXRPC_PLUGIN_JAR_CONFIGURATION,
        mapOf(
            "group" to "org.jetbrains.kotlinx",
            "name" to "kotlinx-rpc-protobuf-plugin",
            "version" to LIBRARY_VERSION,
            "classifier" to "all",
            "ext" to "jar",
        ),
    )
}
