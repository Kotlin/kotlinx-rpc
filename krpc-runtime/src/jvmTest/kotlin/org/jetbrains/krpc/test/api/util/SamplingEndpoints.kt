/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.api.util

import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.client.KRPCClient
import org.jetbrains.krpc.server.KRPCServer
import org.jetbrains.krpc.test.LocalTransport

class SamplingClient(
    config: RPCConfig.Client,
    localTransport: LocalTransport,
) : KRPCClient(config, localTransport.client)

class SamplingServer(
    config: RPCConfig.Server,
    localTransport: LocalTransport,
) : KRPCServer(config, localTransport.server)
