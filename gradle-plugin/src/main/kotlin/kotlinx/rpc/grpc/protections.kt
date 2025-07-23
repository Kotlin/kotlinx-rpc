/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.rpcExtension
import org.gradle.api.GradleException
import org.gradle.api.Project

private const val BUF_PLUGIN_ID = "build.buf"
private const val PROTOBUF_PLUGIN_ID = "com.google.protobuf"

@Suppress("detekt.ThrowsCount")
internal fun Project.configurePluginProtections() {
    var isBufPluginApplied = false
    var isProtobufPluginApplied = false

    pluginManager.withPlugin(BUF_PLUGIN_ID) { isBufPluginApplied = true }
    pluginManager.withPlugin(PROTOBUF_PLUGIN_ID) { isProtobufPluginApplied = true }

    afterEvaluate {
        if (!rpcExtension().grpcApplied.get()) {
            return@afterEvaluate
        }

        if (isBufPluginApplied && !isProtobufPluginApplied) {
            throw GradleException(
                "Buf plugin ($BUF_PLUGIN_ID) can't be applied to the project, " +
                        "it is not compatible with the Rpc Gradle Plugin "
            )
        }

        if (isProtobufPluginApplied && !isBufPluginApplied) {
            throw GradleException(
                "Protobuf plugin ($PROTOBUF_PLUGIN_ID) can't be applied to the project, " +
                        "it is not compatible with the Rpc Gradle Plugin "
            )
        }

        if (isBufPluginApplied) {
            throw GradleException(
                "Both Buf ($BUF_PLUGIN_ID) and Protobuf ($PROTOBUF_PLUGIN_ID) " +
                        "plugins can't be applied to the project, they are not compatible with the Rpc Gradle Plugin "
            )
        }
    }
}
