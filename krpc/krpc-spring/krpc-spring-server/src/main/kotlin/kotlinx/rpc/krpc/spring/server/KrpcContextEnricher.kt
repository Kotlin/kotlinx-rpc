/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import org.springframework.web.socket.WebSocketSession
import kotlin.coroutines.CoroutineContext

public interface KrpcContextEnricher {
    public fun enrich(session: WebSocketSession, coroutineContext: CoroutineContext): CoroutineContext
}