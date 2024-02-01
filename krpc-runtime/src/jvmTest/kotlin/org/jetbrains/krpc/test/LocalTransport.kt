/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.RPCTransportMessage
import kotlin.coroutines.CoroutineContext

class LocalTransport(parentScope: CoroutineScope? = null) : CoroutineScope {
    override val coroutineContext = parentScope?.run {coroutineContext + Job() } ?: Job()

    private val clientIncoming = Channel<RPCTransportMessage>()
    private val serverIncoming = Channel<RPCTransportMessage>()

    val client: RPCTransport = object : RPCTransport {
        override val coroutineContext: CoroutineContext = this@LocalTransport.coroutineContext

        override suspend fun send(message: RPCTransportMessage) {
            serverIncoming.send(message)
        }

        override suspend fun receive(): RPCTransportMessage {
            return clientIncoming.receive()
        }
    }

    val server: RPCTransport = object : RPCTransport {
        override val coroutineContext: CoroutineContext = this@LocalTransport.coroutineContext

        override suspend fun send(message: RPCTransportMessage) {
            clientIncoming.send(message)
        }

        override suspend fun receive(): RPCTransportMessage {
            return serverIncoming.receive()
        }
    }
}
