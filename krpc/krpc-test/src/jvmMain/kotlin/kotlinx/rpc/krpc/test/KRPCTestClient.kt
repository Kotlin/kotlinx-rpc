/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.rpc.krpc.RPCConfig
import kotlinx.rpc.krpc.RPCTransport
import kotlinx.rpc.krpc.client.KRPCClient

/**
 * Implementation of [KRPCClient] that can be used to test custom [RPCTransport].
 *
 * NOTE: one [RPCTransport] is meant to be used by only one client,
 * but this class allows for abuse. Be cautious about how you handle [RPCTransport] with this class.
 */
class KRPCTestClient(
    config: RPCConfig.Client,
    transport: RPCTransport,
) : KRPCClient(config, transport)
