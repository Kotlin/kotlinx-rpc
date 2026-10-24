/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlinx.rpc.krpc.server.KrpcServer

/**
 * Runs one kRPC server for a WebSocket session.
 *
 * The Spring integration makes one server for each session.
 * When the session ends, the server closes.
 * When the server closes, the integration sends the remaining messages and then closes the session.
 */
public class SpringKrpcServer internal constructor(
    config: KrpcConfig.Server,
    private val transport: KrpcWebSocketTransport,
) : KrpcServer(config, transport) {

    internal fun deliver(message: KrpcTransportMessage): Boolean {
        return transport.deliver(message)
    }

    internal fun invokeOnTransportCompletion(handler: () -> Unit) {
        transport.invokeOnCompletion(handler)
    }

    internal fun connectionClosed() {
        transport.connectionClosed()
    }
}
