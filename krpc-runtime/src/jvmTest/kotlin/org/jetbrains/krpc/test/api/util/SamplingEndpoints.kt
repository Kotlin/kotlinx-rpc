/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.api.util

import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.client.KRPCClient
import org.jetbrains.krpc.server.KRPCServer
import org.jetbrains.krpc.test.LocalTransport

class SamplingClient(
    config: RPCConfig.Client,
    localTransport: LocalTransport,
) : KRPCClient(config), RPCTransport by localTransport.client {
    init {
        val engineId = KRPCClient::class.java.declaredFields
            .single { it.name == "connectionId" }
            .apply { isAccessible = true }

        // reproducible ids
        engineId.setLong(this, 1)
    }
}

class SamplingServer(
    config: RPCConfig.Server,
    localTransport: LocalTransport,
) : KRPCServer(config), RPCTransport by localTransport.server
