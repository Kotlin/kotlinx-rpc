/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import kotlinx.rpc.LIBRARY_VERSION
import org.gradle.api.Project

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
