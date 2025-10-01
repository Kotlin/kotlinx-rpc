/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.krpc.internal

internal actual class DeserializedException actual constructor(
    private val toStringMessage: String,
    actual override val message: String,
    stacktrace: List<StackElement>,
    cause: SerializedException?,
    className: String
) : Throwable() {

    override val cause: Throwable? = cause?.deserialize()

    override fun toString(): String = toStringMessage
}


internal actual fun Throwable.stackElements(): List<StackElement> = emptyList()

internal actual fun SerializedException.deserializeUnsafe(): Throwable {
    return cancellationExceptionDeserialize()
        ?: DeserializedException(toStringMessage, message, stacktrace, cause, className)
}
