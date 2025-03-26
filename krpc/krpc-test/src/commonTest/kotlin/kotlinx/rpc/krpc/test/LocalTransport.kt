/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.job
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlin.coroutines.CoroutineContext

class LocalTransport(parentScope: CoroutineScope? = null) : CoroutineScope {
    override val coroutineContext = parentScope
        ?.run { coroutineContext + SupervisorJob(coroutineContext.job) }
        ?: SupervisorJob()

    private val clientIncoming = Channel<KrpcTransportMessage>()
    private val serverIncoming = Channel<KrpcTransportMessage>()

    val client: KrpcTransport = object : KrpcTransport {
        override val coroutineContext: CoroutineContext = this@LocalTransport.coroutineContext +
                Job(this@LocalTransport.coroutineContext.job)

        override suspend fun send(message: KrpcTransportMessage) {
            serverIncoming.send(message)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return clientIncoming.receive()
        }
    }

    val server: KrpcTransport = object : KrpcTransport {
        override val coroutineContext: CoroutineContext = this@LocalTransport.coroutineContext +
                Job(this@LocalTransport.coroutineContext.job)

        override suspend fun send(message: KrpcTransportMessage) {
            clientIncoming.send(message)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return serverIncoming.receive()
        }
    }
}
