/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor.client

import io.ktor.websocket.*
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.client.KRPCClient
import org.jetbrains.krpc.transport.ktor.KtorTransport

internal class KtorRPCClient(
    webSocketSession: WebSocketSession,
    config: RPCConfig.Client,
): KRPCClient(config, KtorTransport(webSocketSession))
