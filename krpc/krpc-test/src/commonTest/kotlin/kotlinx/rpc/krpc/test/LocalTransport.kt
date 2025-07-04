/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.job
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class LocalTransport(
    parentContext: CoroutineContext? = null,
) : CoroutineScope {
    override val coroutineContext = SupervisorJob(parentContext?.get(Job))

    private val clientIncoming = Channel<KrpcTransportMessage>()
    private val serverIncoming = Channel<KrpcTransportMessage>()

    val lastMessageSentOnClient = atomic(0L)
    val lastMessageSentOnServer = atomic(0L)

    val client: KrpcTransport = object : KrpcTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext.job).let {
            if(parentContext != null) parentContext + it else it
        }

        @OptIn(ExperimentalTime::class)
        override suspend fun send(message: KrpcTransportMessage) {
            lastMessageSentOnClient.getAndSet(Clock.System.now().toEpochMilliseconds())
            serverIncoming.send(message)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return clientIncoming.receive()
        }
    }

    val server: KrpcTransport = object : KrpcTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext.job).let {
            if(parentContext != null) parentContext + it else it
        }

        @OptIn(ExperimentalTime::class)
        override suspend fun send(message: KrpcTransportMessage) {
            lastMessageSentOnServer.getAndSet(Clock.System.now().toEpochMilliseconds())
            clientIncoming.send(message)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return serverIncoming.receive()
        }
    }
}
