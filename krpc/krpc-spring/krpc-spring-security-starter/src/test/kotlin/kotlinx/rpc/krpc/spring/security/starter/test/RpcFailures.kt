/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.starter.test

import io.ktor.client.plugins.websocket.WebSocketException
import kotlinx.coroutines.CancellationException
import org.springframework.security.access.AccessDeniedException

internal fun Throwable.causes(): Sequence<Throwable> = generateSequence(this) { it.cause }

// A timeout is a cancellation, so this rejects timeouts too.
internal fun Throwable.isCancellation(): Boolean = causes().any { it is CancellationException }

// The client gets the server exception either as the original type or as a kRPC DeserializedException.
internal fun Throwable.isAccessDenied(): Boolean = causes().any { cause ->
    cause is AccessDeniedException ||
        cause.message?.contains("Access Denied", ignoreCase = true) == true ||
        (cause.javaClass.simpleName == "DeserializedException" &&
            cause.toString().contains("AccessDeniedException"))
}

internal fun Throwable.isUnauthorizedHandshakeFailure(): Boolean = causes().any { cause ->
    cause is WebSocketException && cause.message?.endsWith("but was 401") == true
}
