package org.jetbrains.krpc.test

import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.server.KRPCServer
import kotlin.coroutines.CoroutineContext

class KRPCTestServer(
    config: RPCConfig.Server,
    override val coroutineContext: CoroutineContext,
    private val transport: RPCTransport,
) : KRPCServer(config), RPCTransport by transport
