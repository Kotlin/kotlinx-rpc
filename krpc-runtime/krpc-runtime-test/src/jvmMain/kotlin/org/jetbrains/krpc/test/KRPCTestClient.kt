package org.jetbrains.krpc.test

import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.client.KRPCClient
import org.jetbrains.krpc.RPCTransport
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [KRPCClient] that can be used to test custom [RPCTransport].
 *
 * NOTE: one [RPCTransport] is meant to be used by only one client,
 * but this class allows for abuse. Be cautious about how you handle [RPCTransport] with this class.
 */
class KRPCTestClient(
    config: RPCConfig.Client,
    override val coroutineContext: CoroutineContext,
    transport: RPCTransport,
) : KRPCClient(config), RPCTransport by transport
