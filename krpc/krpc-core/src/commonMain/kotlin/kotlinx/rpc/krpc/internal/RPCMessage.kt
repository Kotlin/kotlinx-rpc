/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.InternalRPCApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalRPCApi
@Serializable
@SerialName("org.jetbrains.krpc.internal.transport.RPCMessage")
public sealed interface RPCMessage {
    //NOTE: It turned out that the connectionId is not necessary when we have
    //     * 1-to-1 client-server connections.
    //     * But I left it for future usages, and now it is always null.
    /**
     * Unique id for the current connection client-server connection.
     */
    public val connectionId: Long?

    /**
     * [pluginParams] map contains additional parameters to send over wire.
     * Map is much easier to update without breaking compatibility,
     * thought being more limiting in terms of content.
     *
     * Use [RPCPluginKey] to retrieve a value.
     * The structure of the values should not change over time to ensure compatibility.
     */
    public val pluginParams: Map<RPCPluginKey, String>?
}

/**
 * Use this message to add new message types without adding new classes to the protocol hierarchy.
 */
@InternalRPCApi
@Serializable
@SerialName("org.jetbrains.krpc.internal.transport.RPCGenericMessage")
public data class RPCGenericMessage(
    override val connectionId: Long?,
    override val pluginParams: Map<RPCPluginKey, String>?
) : RPCMessage {
    @InternalRPCApi
    public companion object {
        public const val CANCELLATION_TYPE: String = "cancellation"
    }
}

@InternalRPCApi
@Serializable
@SerialName("org.jetbrains.krpc.internal.transport.RPCProtocolMessage")
public sealed interface RPCProtocolMessage : RPCMessage {
    override val pluginParams: Map<RPCPluginKey, String>

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.internal.transport.RPCProtocolMessage.Handshake")
    public data class Handshake(
        val supportedPlugins: Set<RPCPlugin>,
        override val connectionId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String> = emptyMap(),
    ) : RPCProtocolMessage

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.internal.transport.RPCProtocolMessage.Failure")
    public data class Failure(
        val errorMessage: String,
        override val connectionId: Long? = null,
        val failedMessage: RPCMessage? = null,
        override val pluginParams: Map<RPCPluginKey, String> = emptyMap(),
    ) : RPCProtocolMessage
}

/**
 * Only service messages (method calls, streams, etc.), name is kept for easier compatibility.
 */
@InternalRPCApi
@Serializable
@SerialName("org.jetbrains.krpc.RPCMessage")
public sealed interface RPCCallMessage : RPCMessage {
    public val callId: String
    public val serviceType: String
    public val serviceId: Long?

    @InternalRPCApi
    @SerialName("org.jetbrains.krpc.internal.transport.RPCMessage.Data")
    public sealed interface Data {
        @InternalRPCApi
        public sealed interface BinaryData : Data {
            public val data: ByteArray
        }

        @InternalRPCApi
        public sealed interface StringData : Data {
            public val data: String
        }
    }

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.RPCMessage.CallResult")
    public sealed interface CallResult : RPCCallMessage

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.internal.transport.RPCMessage.CallData")
    public sealed interface CallData : RPCCallMessage, Data {
        public val callableName: String
        public val callType: CallType?
    }

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.internal.transport.RPCMessage.CallType")
    public enum class CallType {
        Method, Field,
    }

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.RPCMessage.CallData")
    public data class CallDataString(
        override val callId: String,
        override val serviceType: String,
        @SerialName("method")
        override val callableName: String,
        override val callType: CallType?,
        override val data: String,
        override val connectionId: Long? = null,
        override val serviceId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String>? = emptyMap(),
    ) : CallData, Data.StringData

    @InternalRPCApi
    @Suppress("ArrayInDataClass")
    @Serializable
    @SerialName("org.jetbrains.krpc.internal.transport.RPCMessage.CallDataBinary")
    public data class CallDataBinary(
        override val callId: String,
        override val serviceType: String,
        @SerialName("method")
        override val callableName: String,
        override val callType: CallType?,
        override val data: ByteArray,
        override val connectionId: Long? = null,
        override val serviceId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String>? = emptyMap(),
    ) : CallData, Data.BinaryData

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.internal.transport.RPCMessage.CallSuccess")
    public sealed interface CallSuccess : CallResult, Data

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.RPCMessage.CallSuccess")
    public data class CallSuccessString(
        override val callId: String,
        override val serviceType: String,
        override val data: String,
        override val connectionId: Long? = null,
        override val serviceId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String>? = emptyMap(),
    ) : CallSuccess, Data.StringData

    @InternalRPCApi
    @Suppress("ArrayInDataClass")
    @Serializable
    @SerialName("org.jetbrains.krpc.internal.transport.RPCMessage.CallSuccessBinary")
    public data class CallSuccessBinary(
        override val callId: String,
        override val serviceType: String,
        override val data: ByteArray,
        override val connectionId: Long? = null,
        override val serviceId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String>? = emptyMap(),
    ) : CallSuccess, Data.BinaryData

    /**
     * Both for client and server
     */
    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.RPCMessage.CallException")
    public data class CallException(
        override val callId: String,
        override val serviceType: String,
        val cause: SerializedException,
        override val connectionId: Long? = null,
        override val serviceId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String>? = emptyMap(),
    ) : CallResult

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.internal.transport.RPCMessage.StreamMessage")
    public sealed interface StreamMessage : RPCCallMessage, Data {
        public val streamId: String
    }

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.RPCMessage.StreamMessage")
    public data class StreamMessageString(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        override val streamId: String,
        override val data: String,
        override val connectionId: Long? = null,
        override val serviceId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String>? = emptyMap(),
    ) : StreamMessage, Data.StringData

    @InternalRPCApi
    @Suppress("ArrayInDataClass")
    @Serializable
    @SerialName("org.jetbrains.krpc.internal.transport.RPCMessage.StreamMessageBinary")
    public data class StreamMessageBinary(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        override val streamId: String,
        override val data: ByteArray,
        override val connectionId: Long? = null,
        override val serviceId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String>? = emptyMap(),
    ) : StreamMessage, Data.BinaryData

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.RPCMessage.StreamCancel")
    public data class StreamCancel(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        val streamId: String,
        val cause: SerializedException,
        override val connectionId: Long? = null,
        override val serviceId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String>? = emptyMap(),
    ) : RPCCallMessage

    @InternalRPCApi
    @Serializable
    @SerialName("org.jetbrains.krpc.RPCMessage.StreamFinished")
    public data class StreamFinished(
        override val callId: String,
        override val serviceType: String,
        @SerialName("flowId")
        val streamId: String,
        override val connectionId: Long? = null,
        override val serviceId: Long? = null,
        override val pluginParams: Map<RPCPluginKey, String>? = emptyMap(),
    ) : RPCCallMessage
}

@InternalRPCApi
public operator fun RPCMessage.get(key: RPCPluginKey): String? = pluginParams?.get(key)
