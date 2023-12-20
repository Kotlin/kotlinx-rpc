/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal

@InternalKRPCApi
fun serializeException(cause: Throwable): SerializedException {
    val message = cause.message ?: "Unknown exception"
    val stacktrace = cause.stackElements()
    val serializedCause = cause.cause?.let { serializeException(it) }
    val className = cause::class.qualifiedClassNameOrNull ?: ""

    return SerializedException(cause.toString(), message, stacktrace, serializedCause, className)
}

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
