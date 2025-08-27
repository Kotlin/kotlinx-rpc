/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.job
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.krpc.internal.KrpcCallMessage
import kotlinx.rpc.krpc.internal.KrpcConnector
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class KrpcConnectorTest : KrpcConnectorBaseTest() {
    @Test
    fun connectorTest() = runTest { clientConnector, serverConnector ->
        clientConnector.sendMessage("Hello".asCallMessage("1", "test"))
    }
}

abstract class KrpcConnectorBaseTest {
    protected fun String.asCallMessage(
        callId: String,
        serviceId: String,
    ) = KrpcCallMessage.CallDataString(
        connectionId = null,
        callId = callId,
        serviceType = serviceId,
        data = this,
        callableName = "",
        callType = KrpcCallMessage.CallType.Method,
    )

    protected fun runTest(
        waitTimeout: Duration = 10.seconds,
        callTimeout: Duration = 10.seconds,
        perCallBufferSize: Int = 100,
        body: suspend TestScope.(clientConnector: KrpcConnector, serverConnector: KrpcConnector) -> Unit,
    ) = kotlinx.coroutines.test.runTest {
        val connectorConfig = KrpcConfig.Connector(waitTimeout, callTimeout, perCallBufferSize)

        val transport = LocalTransport(coroutineContext)

        val clientConnector = KrpcConnector(
            serialFormat = Json,
            transport = transport.client,
            config = connectorConfig,
            isServer = false,
        )

        val serverConnector = KrpcConnector(
            serialFormat = Json,
            transport = transport.server,
            config = connectorConfig,
            isServer = true,
        )

        body(clientConnector, serverConnector)

        transport.coroutineContext.job.cancelAndJoin()
    }
}

private class LocalTransport(
    parentContext: CoroutineContext? = null,
) : CoroutineScope {
    override val coroutineContext = SupervisorJob(parentContext?.get(Job))

    private val clientIncoming = Channel<KrpcTransportMessage>()
    private val serverIncoming = Channel<KrpcTransportMessage>()

    val client: KrpcTransport = object : KrpcTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext.job).let {
            if(parentContext != null) parentContext + it else it
        }

        override suspend fun send(message: KrpcTransportMessage) {
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

        override suspend fun send(message: KrpcTransportMessage) {
            clientIncoming.send(message)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return serverIncoming.receive()
        }
    }
}
