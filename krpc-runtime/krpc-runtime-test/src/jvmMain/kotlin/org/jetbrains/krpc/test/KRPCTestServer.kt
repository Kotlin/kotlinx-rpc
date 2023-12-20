package org.jetbrains.krpc.test

import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransportMessage
import org.jetbrains.krpc.internal.transport.RPCTransport
import org.jetbrains.krpc.server.KRPCServer
import kotlin.coroutines.CoroutineContext

class KRPCTestServer(
    config: RPCConfig.Server,
    override val coroutineContext: CoroutineContext,
    private val transport: RPCTransport,
    waitForService: Boolean = true,
) : KRPCServer(config, waitForService) {
    override suspend fun send(message: RPCTransportMessage) {
        transport.send(message)
    }

    override suspend fun receive(): RPCTransportMessage {
        return transport.receive()
    }
}
