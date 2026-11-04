/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

/**
 * Configuration for the kRPC Spring server.
 *
 * @property krpcPath The URL path for the WebSocket endpoint.
 */
public data class KrpcProperties(
    val krpcPath: String
)
