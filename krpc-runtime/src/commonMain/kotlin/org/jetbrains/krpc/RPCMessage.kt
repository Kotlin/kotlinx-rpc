package org.jetbrains.krpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RPCMessage {
    val callId: String
    val serviceType: String

    sealed interface Data {
        sealed interface BinaryData: Data {
            val data: ByteArray
        }

        sealed interface StringData: Data {
            val data: String
        }
    }

    @Serializable
    sealed interface CallResult : RPCMessage

    @Serializable
    sealed interface CallData : RPCMessage, Data {
        val callableName: String
    }

    @Serializable
    @SerialName("CallData")
    data class CallDataString(
        override val callId: String,
        override val serviceType: String,
        @SerialName("method")
        override val callableName: String,
        override val data: String,
    ) : CallData, Data.StringData

    @Suppress("ArrayInDataClass")
    @Serializable
    data class CallDataBinary(
        override val callId: String,
        override val serviceType: String,
        @SerialName("method")
        override val callableName: String,
        override val data: ByteArray,
    ) : CallData, Data.BinaryData

    @Serializable
    sealed interface CallSuccess : RPCMessage, Data

    @Serializable
    @SerialName("CallSuccess")
    data class CallSuccessString(
        override val callId: String,
        override val serviceType: String,
        override val data: String
    ) : CallResult, CallSuccess, Data.StringData

    @Suppress("ArrayInDataClass")
    @Serializable
    data class CallSuccessBinary(
        override val callId: String,
        override val serviceType: String,
        override val data: ByteArray
    ) : CallResult, CallSuccess, Data.BinaryData

    /**
     * Both for client and server
     */
    @Serializable
    data class CallException(
        override val callId: String,
        override val serviceType: String,
        val cause: SerializedException
    ) : CallResult

    @Serializable
    sealed interface StreamMessage : RPCMessage, Data {
        val streamId: String
    }

    @Serializable
    @SerialName("StreamMessage")
    data class StreamMessageString(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        override val streamId: String,
        override val data: String
    ) : StreamMessage, Data.StringData

    @Suppress("ArrayInDataClass")
    @Serializable
    data class StreamMessageBinary(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        override val streamId: String,
        override val data: ByteArray
    ) : StreamMessage, Data.BinaryData

    @Serializable
    data class StreamCancel(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        val streamId: String,
        val cause: SerializedException
    ) : RPCMessage

    @Serializable
    data class StreamFinished(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        val streamId: String,
    ) : RPCMessage
}
