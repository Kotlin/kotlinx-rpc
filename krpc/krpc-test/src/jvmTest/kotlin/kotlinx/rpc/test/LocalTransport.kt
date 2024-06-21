/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.job
import kotlinx.rpc.RPCTransport
import kotlinx.rpc.RPCTransportMessage
import kotlin.coroutines.CoroutineContext

class LocalTransport(parentScope: CoroutineScope? = null) : CoroutineScope {
    override val coroutineContext = parentScope?.run { SupervisorJob(coroutineContext.job) }
        ?: SupervisorJob()

    private val clientIncoming = Channel<RPCTransportMessage>()
    private val serverIncoming = Channel<RPCTransportMessage>()

    val client: RPCTransport = object : RPCTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext.job)

        override suspend fun send(message: RPCTransportMessage) {
            serverIncoming.send(message)
        }

        override suspend fun receive(): RPCTransportMessage {
            return clientIncoming.receive()
        }
    }

    val server: RPCTransport = object : RPCTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext)

        override suspend fun send(message: RPCTransportMessage) {
            clientIncoming.send(message)
        }

        override suspend fun receive(): RPCTransportMessage {
            return serverIncoming.receive()
        }
    }
}
