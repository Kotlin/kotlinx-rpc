package org.jetbrains.krpc

import org.jetbrains.krpc.internal.InternalKRPCApi

internal actual class DeserializedException @OptIn(InternalKRPCApi::class)
actual constructor(
    private val toStringMessage: String,
    override val message: String,
    stacktrace: List<StackElement>,
    cause: SerializedException?,
    className: String
) : Throwable() {
    @OptIn(InternalKRPCApi::class)
    override val cause: Throwable? = cause?.deserialize()

    override fun toString(): String = toStringMessage
}

@OptIn(InternalKRPCApi::class)
internal actual fun Throwable.stackElements(): List<StackElement> = emptyList()

@InternalKRPCApi
actual fun SerializedException.deserialize(): Throwable {
    return DeserializedException(toStringMessage, message, stacktrace, cause, className)
}

internal actual val Throwable.qualifiedClassName: String?
    get() = this::class.toString()
