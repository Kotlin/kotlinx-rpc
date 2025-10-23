/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.coroutines.CancellationException
import kotlinx.rpc.internal.rpcInternalTypeName
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public fun serializeException(cause: Throwable): SerializedException {
    val message = cause.message ?: "Unknown exception"
    val stacktrace = cause.stackElements()
    val serializedCause = cause.cause?.let { serializeException(it) }
    val className = if (
        cause is CancellationException || cause is kotlin.coroutines.cancellation.CancellationException
    ) {
        CancellationException::class.rpcInternalTypeName ?: "kotlinx.coroutines.CancellationException"
    } else {
        cause::class.rpcInternalTypeName ?: ""
    }

    return SerializedException(cause.toString(), message, stacktrace, serializedCause, className)
}

internal expect fun Throwable.stackElements(): List<StackElement>

internal expect fun SerializedException.deserializeUnsafe(): Throwable

internal fun SerializedException.cancellationExceptionDeserialize(): CancellationException? {
    if (className == CancellationException::class.rpcInternalTypeName
        || className == kotlin.coroutines.cancellation.CancellationException::class.rpcInternalTypeName
    ) {
        val cause = this@cancellationExceptionDeserialize.cause?.deserializeUnsafe()

        return CancellationException(
            message = message,
            cause = cause,
        )
    }

    return null
}

@InternalRpcApi
public fun SerializedException.deserialize(): Throwable {
    val cause = runCatching {
        deserializeUnsafe()
    }

    return if (cause.isFailure) {
        cause.exceptionOrNull()!!
    } else {
        cause.getOrNull()!!
    }
}

internal expect class DeserializedException(
    toStringMessage: String,
    message: String,
    stacktrace: List<StackElement>,
    cause: SerializedException?,
    className: String,
) : Throwable {
    override val message: String
}

internal fun illegalStateException(message: String, cause: Throwable? = null): IllegalStateException = when (cause) {
    null -> IllegalStateException(message)
    else -> IllegalStateException(message, cause)
}
