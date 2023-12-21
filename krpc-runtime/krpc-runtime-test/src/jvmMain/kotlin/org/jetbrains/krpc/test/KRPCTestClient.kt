package org.jetbrains.krpc.test

import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.client.KRPCClient
import org.jetbrains.krpc.RPCTransport
import kotlin.coroutines.CoroutineContext

class KRPCTestClient(
    config: RPCConfig.Client,
    override val coroutineContext: CoroutineContext,
    private val transport: RPCTransport,
) : KRPCClient(config), RPCTransport by transport
