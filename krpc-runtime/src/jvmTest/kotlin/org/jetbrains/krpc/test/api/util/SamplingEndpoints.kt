/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.api.util

import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.client.KRPCClient
import org.jetbrains.krpc.internal.SeqIdConuter
import org.jetbrains.krpc.server.KRPCServer
import org.jetbrains.krpc.test.LocalTransport

class SamplingClient(
    config: RPCConfig.Client,
    localTransport: LocalTransport,
) : KRPCClient(config), RPCTransport by localTransport.client

class SamplingServer(
    config: RPCConfig.Server,
    localTransport: LocalTransport,
) : KRPCServer(config), RPCTransport by localTransport.server {
    init {
        val idCounterField = KRPCServer::class.java.declaredFields
            .single { it.name == "idCounter" }
            .apply { isAccessible = true }

        // reproducible ids
        idCounterField.set(this, object : SeqIdConuter {
            override fun nextId(): Long {
                return 1
            }
        })
    }
}
