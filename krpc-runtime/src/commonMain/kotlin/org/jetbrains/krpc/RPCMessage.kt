package org.jetbrains.krpc

import kotlinx.serialization.Serializable

@Serializable
sealed interface RPCMessage {
    val callId: String

    @Serializable
    sealed interface CallResult : RPCMessage

    @Serializable
    data class CallData(override val callId: String, val method: String, val data: String) : RPCMessage

    @Serializable
    data class CallSuccess(override val callId: String, val data: String) : CallResult

    /**
     * Both for client and server
     */
    @Serializable
    data class CallException(
        override val callId: String,
        val cause: SerializedException
    ) : CallResult

    @Serializable
    data class StreamMessage(override val callId: String, val flowId: String, val data: String) : RPCMessage

    @Serializable
    data class StreamCancel(override val callId: String, val flowId: String, val cause: SerializedException) :
        RPCMessage

    @Serializable
    data class StreamFinished(override val callId: String, val flowId: String) : RPCMessage
}