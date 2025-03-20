/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.rpcInternalTypeName
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public fun serializeException(cause: Throwable): SerializedException {
    val message = cause.message ?: "Unknown exception"
    val stacktrace = cause.stackElements()
    val serializedCause = cause.cause?.let { serializeException(it) }
    val className = cause::class.rpcInternalTypeName ?: ""

    return SerializedException(cause.toString(), message, stacktrace, serializedCause, className)
}

internal expect fun Throwable.stackElements(): List<StackElement>

@InternalRpcApi
public expect fun SerializedException.deserialize(): Throwable

internal expect class DeserializedException(
    toStringMessage: String,
    message: String,
    stacktrace: List<StackElement>,
    cause: SerializedException?,
    className: String
) : Throwable {
    override val message: String
}
