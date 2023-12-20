package org.jetbrains.krpc.test

import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransportMessage
import org.jetbrains.krpc.client.KRPCClient
import org.jetbrains.krpc.internal.transport.RPCTransport
import kotlin.coroutines.CoroutineContext

class KRPCTestClient(
    config: RPCConfig.Client,
    override val coroutineContext: CoroutineContext,
    private val transport: RPCTransport,
    waitForService: Boolean = true,
) : KRPCClient(config, waitForService) {
    override suspend fun send(message: RPCTransportMessage) {
        transport.send(message)
    }

    override suspend fun receive(): RPCTransportMessage {
        return transport.receive()
    }
}
