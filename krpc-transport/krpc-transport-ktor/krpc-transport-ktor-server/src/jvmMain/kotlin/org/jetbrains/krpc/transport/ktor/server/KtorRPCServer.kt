/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor.server

import io.ktor.websocket.*
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.server.KRPCServer
import org.jetbrains.krpc.transport.ktor.KtorTransport

@InternalKRPCApi
class KtorRPCServer(
    webSocketSession: WebSocketSession,
    config: RPCConfig.Server,
) : KRPCServer(config), RPCTransport by KtorTransport(webSocketSession)
