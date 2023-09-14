package org.jetbrains.krpc

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.Serializable

interface RPCTransport {
    val incoming: SharedFlow<RPCMessage>

    suspend fun send(message: RPCMessage)
}


fun serializeException(cause: Throwable): SerializedException {
    val message = cause.message ?: "Unknown exception"
    val stacktrace = cause.stackElements()
    val serializedCause = cause.cause?.let { serializeException(it) }

    return SerializedException(cause.toString(), message, stacktrace, serializedCause)
}

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
    val cause: SerializedException?
) {
    internal fun deserialize(): Throwable {
        return DeserializedException(toStringMessage, message, stacktrace, cause)
    }
}

expect class DeserializedException(
    toStringMessage: String,
    message: String,
    stacktrace: List<StackElement>,
    cause: SerializedException?
) : Throwable
