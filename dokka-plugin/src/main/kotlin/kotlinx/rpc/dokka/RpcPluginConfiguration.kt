/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.dokka

import java.util.Properties

data class RpcPluginConfiguration(
    val coreVersion: String = "",
    val grpcDevVersion: String = "",
) {
    companion object {
        fun load(): RpcPluginConfiguration {
            val props = Properties()
            val stream = RpcPluginConfiguration::class.java
                .getResourceAsStream("/rpc-dokka-config.properties")
                ?: return RpcPluginConfiguration()
            stream.use { props.load(it) }
            return RpcPluginConfiguration(
                coreVersion = props.getProperty("coreVersion", ""),
                grpcDevVersion = props.getProperty("grpcDevVersion", ""),
            )
        }
    }
}
