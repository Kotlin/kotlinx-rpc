/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal.transport

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.SerializedException

@InternalKRPCApi
@Serializable
public sealed interface RPCMessage {
    public val callId: String
    public val serviceType: String

    public sealed interface Data {
        public sealed interface BinaryData: Data {
            public val data: ByteArray
        }

        public sealed interface StringData: Data {
            public val data: String
        }
    }

    @Serializable
    public sealed interface CallResult : RPCMessage

    @Serializable
    public sealed interface CallData : RPCMessage, Data {
        public val callableName: String
        public val callType: CallType?
    }

    @Serializable
    public enum class CallType {
        Method, Field,
    }

    @Serializable
    @SerialName("CallData")
    public data class CallDataString(
        override val callId: String,
        override val serviceType: String,
        @SerialName("method")
        override val callableName: String,
        override val callType: CallType?,
        override val data: String,
    ) : CallData, Data.StringData

    @Suppress("ArrayInDataClass")
    @Serializable
    public data class CallDataBinary(
        override val callId: String,
        override val serviceType: String,
        @SerialName("method")
        override val callableName: String,
        override val callType: CallType?,
        override val data: ByteArray,
    ) : CallData, Data.BinaryData

    @Serializable
    public sealed interface CallSuccess : RPCMessage, Data

    @Serializable
    @SerialName("CallSuccess")
    public data class CallSuccessString(
        override val callId: String,
        override val serviceType: String,
        override val data: String
    ) : CallResult, CallSuccess, Data.StringData

    @Suppress("ArrayInDataClass")
    @Serializable
    public data class CallSuccessBinary(
        override val callId: String,
        override val serviceType: String,
        override val data: ByteArray
    ) : CallResult, CallSuccess, Data.BinaryData

    /**
     * Both for client and server
     */
    @Serializable
    public data class CallException(
        override val callId: String,
        override val serviceType: String,
        val cause: SerializedException
    ) : CallResult

    @Serializable
    public sealed interface StreamMessage : RPCMessage, Data {
        public val streamId: String
    }

    @Serializable
    @SerialName("StreamMessage")
    public data class StreamMessageString(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        override val streamId: String,
        override val data: String
    ) : StreamMessage, Data.StringData

    @Suppress("ArrayInDataClass")
    @Serializable
    public data class StreamMessageBinary(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        override val streamId: String,
        override val data: ByteArray
    ) : StreamMessage, Data.BinaryData

    @Serializable
    public data class StreamCancel(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        val streamId: String,
        val cause: SerializedException
    ) : RPCMessage

    @Serializable
    public data class StreamFinished(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        val streamId: String,
    ) : RPCMessage
}
