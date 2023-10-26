package org.jetbrains.krpc.internal

import org.jetbrains.krpc.SerializedException
import org.jetbrains.krpc.StackElement

@InternalKRPCApi
fun serializeException(cause: Throwable): SerializedException {
    val message = cause.message ?: "Unknown exception"
    val stacktrace = cause.stackElements()
    val serializedCause = cause.cause?.let { serializeException(it) }
    val className = cause.qualifiedClassName ?: ""

    return SerializedException(cause.toString(), message, stacktrace, serializedCause, className)
}

internal expect val Throwable.qualifiedClassName: String?

internal expect fun Throwable.stackElements(): List<StackElement>

@InternalKRPCApi
expect fun SerializedException.deserialize(): Throwable

internal expect class DeserializedException(
    toStringMessage: String,
    message: String,
    stacktrace: List<StackElement>,
    cause: SerializedException?,
    className: String
) : Throwable
