package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.Serializable

interface RPCTransport : CoroutineScope {
    val incoming: SharedFlow<RPCMessage>

    suspend fun send(message: RPCMessage)
}


fun serializeException(cause: Throwable): SerializedException {
    val message = cause.message ?: "Unknown exception"
    val stacktrace = cause.stackElements()
    val serializedCause = cause.cause?.let { serializeException(it) }
    val className = cause.qualifiedClassName ?: ""

    return SerializedException(cause.toString(), message, stacktrace, serializedCause, className)
}

internal expect val Throwable.qualifiedClassName: String?

internal expect fun Throwable.stackElements(): List<StackElement>

@Serializable
data class StackElement(
    val clazz: String,
    val method: String,
    val fileName: String?,
    val lineNumber: Int
)

@Serializable
class SerializedException(
    val toStringMessage: String,
    val message: String,
    val stacktrace: List<StackElement>,
    val cause: SerializedException?,
    val className: String
) {
}

expect fun SerializedException.deserialize(): Throwable

expect class DeserializedException(
    toStringMessage: String,
    message: String,
    stacktrace: List<StackElement>,
    cause: SerializedException?,
    className: String
) : Throwable
