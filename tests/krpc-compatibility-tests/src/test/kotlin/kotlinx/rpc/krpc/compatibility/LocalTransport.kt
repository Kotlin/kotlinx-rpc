/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.compatibility

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.job
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlinx.rpc.krpc.client.KrpcClient
import kotlinx.rpc.krpc.server.KrpcServer
import kotlin.coroutines.CoroutineContext

class KrpcTestServer(
    config: KrpcConfig.Server,
    transport: KrpcTransport,
) : KrpcServer(config, transport)

class KrpcTestClient(
    config: KrpcConfig.Client,
    transport: KrpcTransport,
) : KrpcClient(config, transport)

class LocalTransport(parentScope: CoroutineScope? = null) : CoroutineScope {
    override val coroutineContext = parentScope?.run { SupervisorJob(coroutineContext.job) }
        ?: SupervisorJob()

    private val clientIncoming = Channel<KrpcTransportMessage>()
    private val serverIncoming = Channel<KrpcTransportMessage>()

    val client: KrpcTransport = object : KrpcTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext.job)

        override suspend fun send(message: KrpcTransportMessage) {
            serverIncoming.send(message)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return clientIncoming.receive()
        }
    }

    val server: KrpcTransport = object : KrpcTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext)

        override suspend fun send(message: KrpcTransportMessage) {
            clientIncoming.send(message)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return serverIncoming.receive()
        }
    }
}
