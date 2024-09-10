/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.InternalRPCApi

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

@InternalRPCApi
public actual fun SerializedException.deserialize(): Throwable {
    return DeserializedException(toStringMessage, message, stacktrace, cause, className)
}
